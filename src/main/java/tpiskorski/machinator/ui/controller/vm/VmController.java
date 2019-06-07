package tpiskorski.machinator.ui.controller.vm;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.machinator.flow.quartz.vm.VmActionScheduler;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineService;
import tpiskorski.machinator.model.vm.VirtualMachineState;
import tpiskorski.machinator.ui.control.ConfirmationAlertFactory;
import tpiskorski.machinator.ui.control.NotificationFactory;
import tpiskorski.machinator.ui.control.VirtualMachineRowFactory;

import java.util.List;

@Controller
public class VmController {

    @Autowired private VirtualMachineService virtualMachineService;
    @Autowired private VirtualMachineRowFactory virtualMachineRowFactory;
    @Autowired private VmActionScheduler vmActionScheduler;
    @Autowired private NotificationFactory notificationFactory;

    @FXML private TableView<VirtualMachine> virtualMachines;

    @FXML private Button removeVmButton;
    @FXML private Button resetVmButton;
    @FXML private Button powerOffVmButton;
    @FXML private Button turnOffVmButton;
    @FXML private Button turnOnVmButton;

    private FilteredList<VirtualMachine> filterableVirtualMachines;

    public FilteredList<VirtualMachine> getFilterableVirtualMachines() {
        return filterableVirtualMachines;
    }

    @FXML
    public void initialize() {
        virtualMachines.setRowFactory(virtualMachineRowFactory);
        setupDisableBindings();

        filterableVirtualMachines = new FilteredList<>(virtualMachineService.getVms(), p -> true);
        virtualMachines.setItems(filterableVirtualMachines);

        virtualMachines.getItems().addListener((ListChangeListener<VirtualMachine>) change -> {
            virtualMachines.getSelectionModel().clearSelection();
        });

        virtualMachines.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void setupDisableBindings() {
        ObservableList<VirtualMachine> selectedItems = virtualMachines.getSelectionModel().getSelectedItems();
        BooleanBinding selectedUnreachableVm = createUnreachableVmBinding();

        removeVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm));
        resetVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm).or(disableIfAny(VirtualMachineState.POWEROFF)));
        powerOffVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm).or(disableIfAny(VirtualMachineState.POWEROFF)));
        turnOffVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm).or(disableIfAny(VirtualMachineState.POWEROFF)));
        turnOnVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm).or(disableIfAny(VirtualMachineState.RUNNING)));
    }

    private BooleanBinding createUnreachableVmBinding() {
        return Bindings.createBooleanBinding(() -> {
            boolean disableChangeType = false;
            VirtualMachine vm = virtualMachines.getSelectionModel().getSelectedItem();

            if (vm == null || vm.getState() == VirtualMachineState.UNREACHABLE ||
                vm.getState() == VirtualMachineState.COMMAND_IN_PROGRESS ||
                vm.getState() == VirtualMachineState.NODE_NOT_REACHABLE) {

                disableChangeType = true;
            }
            return disableChangeType;
        }, virtualMachines.getSelectionModel().selectedItemProperty());
    }

    private BooleanBinding disableIfAny(VirtualMachineState state) {
        return Bindings.createBooleanBinding(() -> {
            boolean disableChangeType = false;

            ObservableList<VirtualMachine> selectedItems = virtualMachines.getSelectionModel().getSelectedItems();

            for (VirtualMachine selectedItem : selectedItems) {
                if (selectedItem.getState() == state) {
                    disableChangeType = true;
                    return disableChangeType;
                }
            }
            return disableChangeType;
        }, virtualMachines.getSelectionModel().selectedItemProperty());
    }

    @FXML
    public void turnOnVm() {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to turn on this vm(s)?",
            "VM"
        );

        if (confirmed) {
            List<VirtualMachine> selectedVm = virtualMachines.getSelectionModel().getSelectedItems();
            selectedVm.forEach(vmActionScheduler::scheduleTurnOn);

            notificationFactory.createAndShow(String.format("Scheduled %s vm(s) for turn on", selectedVm.size()));
        }
    }

    @FXML
    public void turnOffVm() {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to turn off this vm(s)?",
            "VM"
        );

        if (confirmed) {
            List<VirtualMachine> selectedVm = virtualMachines.getSelectionModel().getSelectedItems();
            selectedVm.forEach(vmActionScheduler::scheduleTurnOff);

            notificationFactory.createAndShow(String.format("Scheduled %s vm(s) for turn off", selectedVm.size()));
        }
    }

    @FXML
    public void powerOffVm() {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to power off this vm(s)?",
            "VM"
        );

        if (confirmed) {
            List<VirtualMachine> selectedVm = virtualMachines.getSelectionModel().getSelectedItems();
            selectedVm.forEach(vmActionScheduler::schedulePowerOff);

            notificationFactory.createAndShow(String.format("Scheduled %s vm(s) for power off", selectedVm.size()));
        }
    }

    @FXML
    public void resetVm() {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to reset this vm(s)?",
            "VM"
        );

        if (confirmed) {
            List<VirtualMachine> selectedVm = virtualMachines.getSelectionModel().getSelectedItems();
            selectedVm.forEach(vmActionScheduler::scheduleReset);

            notificationFactory.createAndShow(String.format("Scheduled %s vm(s) for reset", selectedVm.size()));
        }
    }

    @FXML
    public void deleteVm() {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to delete this vm(s)?",
            "VM"
        );

        if (confirmed) {
            List<VirtualMachine> selectedVm = virtualMachines.getSelectionModel().getSelectedItems();
            selectedVm.forEach(vmActionScheduler::scheduleDelete);

            notificationFactory.createAndShow(String.format("Scheduled %s vm(s) for delete", selectedVm.size()));
        }
    }
}
