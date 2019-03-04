package com.github.tpiskorski.vboxcm.ui.controller;

import com.github.tpiskorski.vboxcm.core.job.Job;
import com.github.tpiskorski.vboxcm.core.job.JobService;
import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineService;
import com.github.tpiskorski.vboxcm.ui.control.ServerCellFactory;
import com.github.tpiskorski.vboxcm.ui.control.VirtualMachineRowFactory;
import com.github.tpiskorski.vboxcm.ui.core.ContextAwareSceneLoader;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
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

        removeVmButton.disableProperty().bind(Bindings.isEmpty(virtualMachines.getSelectionModel().getSelectedItems()));
        resetVmButton.disableProperty().bind(Bindings.isEmpty(virtualMachines.getSelectionModel().getSelectedItems()));
        powerOffVmButton.disableProperty().bind(Bindings.isEmpty(virtualMachines.getSelectionModel().getSelectedItems()));
        turnOffVmButton.disableProperty().bind(Bindings.isEmpty(virtualMachines.getSelectionModel().getSelectedItems()));
        turnOnVmButton.disableProperty().bind(Bindings.isEmpty(virtualMachines.getSelectionModel().getSelectedItems()));

        removeServerButton.disableProperty().bind(Bindings.isEmpty(serverList.getSelectionModel().getSelectedItems()));
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
                if (person.getAddress().get().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else return person.getAddress().get().toLowerCase().contains(lowerCaseFilter);
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
                if (person.getServer().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else return person.getServer().toLowerCase().contains(lowerCaseFilter);
            });
        });

        serverList.setItems(filterableServers);
        virtualMachines.setItems(filterableVirtualMachines);
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
}
