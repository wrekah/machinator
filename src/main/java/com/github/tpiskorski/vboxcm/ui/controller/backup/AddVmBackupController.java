package com.github.tpiskorski.vboxcm.ui.controller.backup;

import com.github.tpiskorski.vboxcm.core.backup.Backup;
import com.github.tpiskorski.vboxcm.core.backup.BackupService;
import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineService;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalTime;

@DependsOn("workbenchController")
@Controller
public class AddVmBackupController {

    public TextField frequency;
    public Button cancelButton;
    public Button addButton;
    public DatePicker firstBackup;
    public TextField backupTime;
    public TextField fileLimit;

    @Autowired private ServerService serverService;
    @Autowired private VirtualMachineService virtualMachineService;
    @Autowired private BackupService backupService;

    @FXML private ComboBox<Server> serverComboBox;
    @FXML private ComboBox<VirtualMachine> vmComboBox;

    @FXML
    public void initialize() {

        frequency.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                frequency.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        setConverters();

        vmComboBox.disableProperty().bind(serverComboBox.valueProperty().isNull());
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
        clear();
        stage.close();
    }

    @FXML
    public void add() {
        Server server = serverComboBox.getSelectionModel().getSelectedItem();
        VirtualMachine vm = vmComboBox.getSelectionModel().getSelectedItem();
        LocalDate backupDay = firstBackup.getValue();
        Integer every = Integer.parseInt(frequency.getText());
        LocalTime backupTime = LocalTime.parse(this.backupTime.getText());
        int fileLimit = Integer.parseInt(this.fileLimit.getText());

        Backup backup = new Backup();
        backup.setServer(server.getAddress());
        backup.setVm(vm.getVmName());
        backup.setFirstBackupDay(backupDay);
        backup.setFrequency(every);
        backup.setBackupTime(backupTime);
        backup.setFileLimit(fileLimit);

        backupService.add(backup);
        ((Stage) addButton.getScene().getWindow()).close();
        clear();
    }

    public void clear() {
        serverComboBox.getSelectionModel().clearSelection();
        vmComboBox.getSelectionModel().clearSelection();
        firstBackup.getEditor().clear();
        frequency.clear();
        backupTime.clear();
        fileLimit.clear();
    }
}
