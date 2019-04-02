package tpiskorski.vboxcm.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.vboxcm.core.job.Job;
import tpiskorski.vboxcm.core.job.JobService;
import tpiskorski.vboxcm.core.vm.VirtualMachine;
import tpiskorski.vboxcm.core.vm.VirtualMachineService;
import tpiskorski.vboxcm.core.vm.VirtualMachineState;
import tpiskorski.vboxcm.ui.control.VirtualMachineRowFactory;

import java.time.LocalDateTime;

@Controller
public class VmController {

    @FXML private Alert turnOnAlert;
    @FXML private Alert turnOffAlert;
    @FXML private Alert powerOffAlert;
    @FXML private Alert resetAlert;
    @FXML private Alert deleteAlert;

    @Autowired private JobService jobService;
    @Autowired private VirtualMachineService virtualMachineService;
    @Autowired private VirtualMachineRowFactory virtualMachineRowFactory;

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
        turnOnAlert.showAndWait();

        if (turnOnAlert.getResult() == ButtonType.NO) {
            return;
        }

        VirtualMachine selectedItem = virtualMachines.getSelectionModel().getSelectedItem();
        Job job = new Job();
        job.setJobName("Turn on vm: " + selectedItem.getVmName());
        job.setProgress("Started");
        job.setStartTime(LocalDateTime.now());
        job.setStatus("In progress");
        jobService.add(job);
    }

    @FXML
    public void turnOffVm() {

        turnOffAlert.showAndWait();

        if (turnOffAlert.getResult() == ButtonType.NO) {
            return;
        }
    }

    @FXML
    public void powerOffVm() {

        powerOffAlert.showAndWait();

        if (powerOffAlert.getResult() == ButtonType.NO) {
            return;
        }
    }

    @FXML
    public void resetVm() {
        resetAlert.showAndWait();

        if (resetAlert.getResult() == ButtonType.NO) {
            return;
        }
    }

    @FXML
    public void deleteVm() {
        deleteAlert.showAndWait();

        if (deleteAlert.getResult() == ButtonType.NO) {
            return;
        }
    }
}
