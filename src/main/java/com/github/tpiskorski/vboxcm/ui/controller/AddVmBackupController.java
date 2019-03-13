package com.github.tpiskorski.vboxcm.ui.controller;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineService;
import com.github.tpiskorski.vboxcm.ui.core.ContextAwareSceneLoader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@DependsOn("workbenchController")
@Controller
public class AddVmBackupController {

    public TextField frequency;
    public Button cancelButton;
    @Autowired private ServerService serverService;
    @Autowired private VirtualMachineService virtualMachineService;
    @Autowired private ContextAwareSceneLoader contextAwareSceneLoader;

    @FXML private ComboBox<Server> serverComboBox;
    @FXML private ComboBox<VirtualMachine> vmComboBox;

    @FXML
    public void initialize() throws IOException {

        frequency.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                frequency.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        setConverters();

        serverComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, server, t1) -> {
            if (t1 != null) {

            }
        });

        vmComboBox.disableProperty().bind(serverComboBox.valueProperty().isNull());
        vmComboBox.disableProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                System.out.println(oldValue);
                System.out.println(newValue);
            }
        });
//        vmComboBox.disableProperty().addListener((observableValue, aBoolean, t1) -> {
//            if(t1){
//                vmComboBox.setPromptText("Pick server to pick vm");
//            }else{
//                vmComboBox.setPromptText("Pick server to pick vm");
//
//            }
//        });

        serverComboBox.setItems(serverService.getServers());

        serverComboBox.getItems().addListener((ListChangeListener<Server>) change -> {
            serverComboBox.getSelectionModel().clearSelection();
        });
        vmComboBox.setItems(virtualMachineService.getVms());
    }

    private void setConverters() {
        serverComboBox.setConverter(
            new StringConverter<>() {

                @Override public String toString(Server server) {
                    if (server == null) {
                        return null;
                    } else {
                        return server.getAddress();
                    }
                }

                @Override public Server fromString(String s) {
                    return null;
                }
            }
        );

        vmComboBox.setConverter(
            new StringConverter<>() {
                @Override public String toString(VirtualMachine virtualMachine) {
                    if (virtualMachine == null) {
                        return null;
                    } else {
                        return virtualMachine.getVmName();
                    }
                }

                @Override public VirtualMachine fromString(String s) {
                    return null;
                }
            });
    }

    @FXML
    public void close() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
