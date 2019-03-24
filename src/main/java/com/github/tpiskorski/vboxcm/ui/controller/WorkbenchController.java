package com.github.tpiskorski.vboxcm.ui.controller;

import com.github.tpiskorski.vboxcm.core.job.Job;
import com.github.tpiskorski.vboxcm.core.job.JobService;
import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineService;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineState;
import com.github.tpiskorski.vboxcm.ui.control.ServerCellFactory;
import com.github.tpiskorski.vboxcm.ui.control.VirtualMachineRowFactory;
import com.github.tpiskorski.vboxcm.ui.core.ContextAwareSceneLoader;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;

@Controller
public class WorkbenchController {

    @Autowired private ContextAwareSceneLoader contextAwareSceneLoader;
    @Autowired private VirtualMachineRowFactory virtualMachineRowFactory;
    @Autowired private ServerCellFactory serverCellFactory;

    @Autowired private ServerService serverService;
    @Autowired private VirtualMachineService virtualMachineService;
    @Autowired private JobService jobService;

    @FXML private BorderPane workbenchPane;
    @FXML private Button removeServerButton;
    @FXML private Button removeVmButton;
    @FXML private Button resetVmButton;
    @FXML private Button powerOffVmButton;
    @FXML private Button turnOffVmButton;
    @FXML private Button turnOnVmButton;
    @FXML private TableView<VirtualMachine> virtualMachines;
    @FXML private TextField filterField;
    @FXML private ListView<Server> serverList;

    private Stage addServerStage;
    private Stage jobsStage;

    public void disableMainWindow() {
        workbenchPane.setDisable(true);
    }

    public void enableMainWindow() {
        workbenchPane.setDisable(false);
    }

    @FXML
    public void initialize() throws IOException {
        serverList.setCellFactory(serverCellFactory);
        virtualMachines.setRowFactory(virtualMachineRowFactory);

        addServerStage = contextAwareSceneLoader.load("/fxml/addServer.fxml");
        addServerStage.setTitle("Adding server...");

        jobsStage = contextAwareSceneLoader.load("/fxml/jobs.fxml");
        jobsStage.setTitle("Jobs");

        setDisableBindings();
        setInputBindings();

        FilteredList<Server> filterableServers = new FilteredList<>(serverService.getServers(), p -> true);
        FilteredList<VirtualMachine> filterableVirtualMachines = new FilteredList<>(virtualMachineService.getVms(), p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterableServers.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                // Filter matches last name.
                if (person.getAddress().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else return person.getAddress().toLowerCase().contains(lowerCaseFilter);
            });
        });

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterableVirtualMachines.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                // Filter matches last name.
                if (person.getServer().getSimpleAddress().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else return person.getServer().getSimpleAddress().contains(lowerCaseFilter);
            });
        });


        serverList.setItems(new SortedList<>(filterableServers, Comparator.comparing(Server::getServerType)));
        virtualMachines.setItems(filterableVirtualMachines);

        virtualMachines.getItems().addListener((ListChangeListener<VirtualMachine>) change -> {
            virtualMachines.getSelectionModel().clearSelection();
        });
    }

    private void setDisableBindings() {
        ObservableList<VirtualMachine> selectedItems = virtualMachines.getSelectionModel().getSelectedItems();
        BooleanBinding selectedUnreachableVm = createUnreachableVmBinding();

        removeVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm));
        resetVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm));
        powerOffVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm));
        turnOffVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm));
        turnOnVmButton.disableProperty().bind(Bindings.isEmpty(selectedItems).or(selectedUnreachableVm));

        removeServerButton.disableProperty().bind(Bindings.isEmpty(serverList.getSelectionModel().getSelectedItems()));
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

    private void setInputBindings() {
        serverList.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                serverList.getSelectionModel().clearSelection();
            }
        });

        serverList.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                serverList.getSelectionModel().clearSelection();
            }
        });
    }

    public void removeServer() {
        Server serverToRemove = serverList.getSelectionModel().getSelectedItem();
        serverService.remove(serverToRemove);
    }

    public void addServer() {
        if (addServerStage.isShowing()) {
            addServerStage.hide();
        } else {
            addServerStage.show();
        }
    }

    public void turnOnVm() {
        VirtualMachine selectedItem = virtualMachines.getSelectionModel().getSelectedItem();
        Job job = new Job();
        job.setJobName("Turn on vm: " + selectedItem.getVmName());
        job.setProgress("Started");
        job.setStartTime(LocalDateTime.now());
        job.setStatus("In progress");
        jobService.add(job);
    }

    public void showJobs(ActionEvent event) {
        if (jobsStage.isShowing()) {
            jobsStage.hide();
        } else {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            jobsStage.setX(currentStage.getX() + currentStage.getWidth());
            jobsStage.setY(currentStage.getY());
            jobsStage.show();
        }
    }
}
