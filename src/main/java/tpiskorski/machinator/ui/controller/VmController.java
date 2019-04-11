package tpiskorski.machinator.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.machinator.core.vm.VirtualMachine;
import tpiskorski.machinator.core.vm.VirtualMachineService;
import tpiskorski.machinator.core.vm.VirtualMachineState;
import tpiskorski.machinator.quartz.vm.VmActionScheduler;
import tpiskorski.machinator.ui.control.ConfirmationAlertFactory;
import tpiskorski.machinator.ui.control.VirtualMachineRowFactory;

import java.io.IOException;

@Controller
public class VmController {

    @Autowired private VirtualMachineService virtualMachineService;
    @Autowired private VirtualMachineRowFactory virtualMachineRowFactory;
    @Autowired private VmActionScheduler vmActionScheduler;

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
    }

    private void setupDisableBindings() {
        ObservableList<VirtualMachine> selectedItems = virtualMachines.getSelectionModel().getSelectedItems();
        BooleanBinding selectedUnreachableVm = createUnreachableVmBinding();

        removeVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm));
        resetVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm));
        powerOffVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm));
        turnOffVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm));
        turnOnVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm));
    }

    private BooleanBinding createUnreachableVmBinding() {
        return Bindings.createBooleanBinding(() -> {
            boolean disableChangeType = false;
            VirtualMachine vm = virtualMachines.getSelectionModel().getSelectedItem();
            if (vm == null || vm.getState() == VirtualMachineState.UNREACHABLE) {
                disableChangeType = true;
            }
            return disableChangeType;
        }, virtualMachines.getSelectionModel().selectedItemProperty());
    }

    @FXML
    public void turnOnVm() {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to turn on this vm?",
            "VM"
        );

        if (confirmed) {
            VirtualMachine selectedItem = virtualMachines.getSelectionModel().getSelectedItem();
            vmActionScheduler.scheduleTurnOn(selectedItem);
        }
    }

    @FXML
    public void turnOffVm() throws IOException, InterruptedException {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to turn off this vm?",
            "VM"
        );

        if (confirmed) {
            VirtualMachine selectedItem = virtualMachines.getSelectionModel().getSelectedItem();
            vmActionScheduler.scheduleTurnOff(selectedItem);
        }
    }

    @FXML
    public void powerOffVm() {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to power off this vm?",
            "VM"
        );

        if (confirmed) {
            VirtualMachine selectedItem = virtualMachines.getSelectionModel().getSelectedItem();
            vmActionScheduler.schedulePowerOff(selectedItem);
        }
    }

    @FXML
    public void resetVm() {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to reset this vm?",
            "VM"
        );

        if (confirmed) {
            VirtualMachine selectedItem = virtualMachines.getSelectionModel().getSelectedItem();
            vmActionScheduler.scheduleReset(selectedItem);
        }
    }

    @FXML
    public void deleteVm() {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to delete this vm?",
            "VM"
        );

        if (confirmed) {
            VirtualMachine selectedItem = virtualMachines.getSelectionModel().getSelectedItem();
            vmActionScheduler.scheduleDelete(selectedItem);
        }
    }
}
