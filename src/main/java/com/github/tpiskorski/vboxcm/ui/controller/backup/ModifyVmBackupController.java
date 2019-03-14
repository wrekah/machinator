package com.github.tpiskorski.vboxcm.ui.controller.backup;

import com.github.tpiskorski.vboxcm.core.backup.Backup;
import com.github.tpiskorski.vboxcm.core.backup.BackupService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalTime;

@Controller
public class ModifyVmBackupController {

    @FXML private Button modifyButton;
    @FXML private Button cancelButton;
    @FXML private TextField fileLimit;
    @FXML private TextField backupTime;
    @FXML private TextField frequency;
    @FXML private DatePicker firstBackup;
    @FXML private TextField vmComboBox;
    @FXML private TextField serverComboBox;

    @Autowired private BackupService backupService;

    @FXML
    public void modify() {
        Backup backup = new Backup();
        backup.setServer(serverComboBox.getText());
        backup.setVm(vmComboBox.getText());
        backup.setFirstBackupDay(firstBackup.getValue());
        backup.setFrequency(Integer.parseInt(frequency.getText()));
        backup.setBackupTime(LocalTime.parse(backupTime.getText()));
        backup.setFileLimit(Integer.parseInt(fileLimit.getText()));

        backupService.update(backup);

        ((Stage) modifyButton.getScene().getWindow()).close();
    }

    @FXML
    public void close() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    void clear() {
        serverComboBox.clear();
        vmComboBox.clear();
        firstBackup.getEditor().clear();
        frequency.clear();
        backupTime.clear();
        fileLimit.clear();
    }

    void prepareFor(Backup backup) {
        serverComboBox.setText(backup.getServer());
        vmComboBox.setText(backup.getVm());
        firstBackup.getEditor().setText(backup.getFirstBackupDay().toString());
        frequency.setText("" + backup.getFrequency());
        backupTime.setText(backup.getBackupTime().toString());
        fileLimit.setText("" + backup.getFileLimit());
    }
}
