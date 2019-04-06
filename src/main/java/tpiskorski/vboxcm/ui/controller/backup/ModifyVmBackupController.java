package tpiskorski.vboxcm.ui.controller.backup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.vboxcm.core.backup.BackupDefinition;
import tpiskorski.vboxcm.core.backup.BackupDefinitionService;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class ModifyVmBackupController {

    private final BackupDefinitionService backupDefinitionService;

    @FXML private Button modifyButton;
    @FXML private Button cancelButton;
    @FXML private TextField fileLimit;
    @FXML private TextField backupTime;
    @FXML private TextField frequency;
    @FXML private DatePicker firstBackup;
    @FXML private TextField vmComboBox;
    @FXML private TextField serverComboBox;

    private BackupDefinition savedBackupDefinition;

    @Autowired public ModifyVmBackupController(BackupDefinitionService backupDefinitionService) {
        this.backupDefinitionService = backupDefinitionService;
    }

    @FXML
    public void modify() {
        BackupDefinition backupDefinition = new BackupDefinition(savedBackupDefinition.getServer(), savedBackupDefinition.getVm());

        backupDefinition.setFirstBackupDay(LocalDate.parse(firstBackup.getEditor().getText()));
        backupDefinition.setFrequency(Integer.parseInt(frequency.getText()));
        backupDefinition.setBackupTime(LocalTime.parse(backupTime.getText()));
        backupDefinition.setFileLimit(Integer.parseInt(fileLimit.getText()));

        backupDefinitionService.update(backupDefinition);

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

    void prepareFor(BackupDefinition backupDefinition) {
        savedBackupDefinition = backupDefinition;

        serverComboBox.setText(backupDefinition.getServer().toString());
        vmComboBox.setText(backupDefinition.getVm().toString());
        firstBackup.getEditor().setText(backupDefinition.getFirstBackupDay().toString());
        frequency.setText("" + backupDefinition.getFrequency());
        backupTime.setText(backupDefinition.getBackupTime().toString());
        fileLimit.setText("" + backupDefinition.getFileLimit());
    }
}
