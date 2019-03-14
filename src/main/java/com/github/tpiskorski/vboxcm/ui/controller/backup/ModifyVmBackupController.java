package com.github.tpiskorski.vboxcm.ui.controller.backup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

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

    public void modify() {
    }

    @FXML
    public void close() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
