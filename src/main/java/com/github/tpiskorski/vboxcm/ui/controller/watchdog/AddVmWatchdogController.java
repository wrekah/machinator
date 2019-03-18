package com.github.tpiskorski.vboxcm.ui.controller.watchdog;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.watchdog.Watchdog;
import com.github.tpiskorski.vboxcm.core.watchdog.WatchdogService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AddVmWatchdogController {

    @FXML private ComboBox<Server> serverComboBox;
    @FXML private ComboBox<VirtualMachine> vmComboBox;
    @FXML private ComboBox<Server> backupServerComboBox;

    @FXML private Button addButton;
    @FXML private Button cancelButton;

    @Autowired private WatchdogService watchdogService;

    @FXML
    public void add() {
        Server server = serverComboBox.getSelectionModel().getSelectedItem();
        VirtualMachine vm = vmComboBox.getSelectionModel().getSelectedItem();
        Server backupServer = backupServerComboBox.getSelectionModel().getSelectedItem();

        Watchdog watchdog = new Watchdog();
        watchdog.setServer(server.getAddress());
        watchdog.setVmName(vm.getVmName());
        watchdog.setWatchdogServer(backupServer.getAddress());

        watchdogService.add(watchdog);

        ((Stage) addButton.getScene().getWindow()).close();
        clear();
    }

    @FXML
    public void close() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        clear();
        stage.close();
    }

    private void clear() {
        backupServerComboBox.getSelectionModel().clearSelection();
        vmComboBox.getSelectionModel().clearSelection();
        serverComboBox.getSelectionModel().clearSelection();
    }
}
