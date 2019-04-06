package tpiskorski.vboxcm.ui.controller.backup;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.vboxcm.config.Config;
import tpiskorski.vboxcm.config.ConfigService;
import tpiskorski.vboxcm.core.backup.BackupDefinition;
import tpiskorski.vboxcm.core.backup.BackupDefinitionService;
import tpiskorski.vboxcm.ui.control.ConfirmationAlertFactory;
import tpiskorski.vboxcm.ui.core.ContextAwareSceneLoader;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Controller
public class BackupController {

    public TableView<BackupDefinition> backupsTableView;
    public Button removeVmButton;
    public Button modifyButton;

    @FXML private TextField backupLocation;
    @Autowired private ConfigService configService;

    @Autowired private ContextAwareSceneLoader contextAwareSceneLoader;
    @Autowired private BackupDefinitionService backupDefinitionService;

    private Stage addServerStage;
    private Stage modifyVmStage;

    @Autowired private ModifyVmBackupController modifyVmBackupController;

    @FXML private ContextMenu contextMenu;
    @FXML private MenuItem dynamicMenuItem;

    @FXML
    public void initialize() throws IOException {
        backupsTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                BackupDefinition selectedItem = backupsTableView.getSelectionModel().getSelectedItem();
                if (selectedItem.isActive()) {
                    dynamicMenuItem.setText("Deactivate");
                    dynamicMenuItem.setOnAction(this::deactivate);
                } else {
                    dynamicMenuItem.setText("Activate");
                    dynamicMenuItem.setOnAction(this::activate);
                }

                contextMenu.show(backupsTableView, t.getScreenX(), t.getScreenY());
            }
        });

        backupLocation.setText(configService.getConfig().getBackupLocation());
        configService.addPropertyChangeListener(evt -> {
            Config newValue = (Config) evt.getNewValue();
            backupLocation.setText(newValue.getBackupLocation());
        });

        removeVmButton.disableProperty().bind(Bindings.isEmpty(backupsTableView.getSelectionModel().getSelectedItems()));
        modifyButton.disableProperty().bind(Bindings.isEmpty(backupsTableView.getSelectionModel().getSelectedItems()));

        addServerStage = contextAwareSceneLoader.loadPopup("/fxml/backup/addVmBackup.fxml");
        addServerStage.setTitle("Adding backup...");

        modifyVmStage = contextAwareSceneLoader.loadPopup("/fxml/backup/modifyVmBackup.fxml");
        modifyVmStage.setTitle("Modifying backup...");

        backupsTableView.setItems(backupDefinitionService.getBackups());
    }

    private void deactivate(ActionEvent actionEvent) {
        boolean confirmed = ConfirmationAlertFactory.createAndShow(
            "Do you really want to deactivate this backup?",
            "Watchdog"
        );

        if (confirmed) {
            BackupDefinition backupToDeactivate = backupsTableView.getSelectionModel().getSelectedItem();
            backupDefinitionService.deactivate(backupToDeactivate);
        }
    }

    private void activate(ActionEvent actionEvent) {
        boolean confirmed = ConfirmationAlertFactory.createAndShow(
            "Do you really want to activate this backup?",
            "Watchdog"
        );

        if (confirmed) {
            BackupDefinition backupToDeactivate = backupsTableView.getSelectionModel().getSelectedItem();
            backupDefinitionService.activate(backupToDeactivate);
        }
    }

    @FXML
    public void browseHomeDirectory() {
        try {
            Desktop.getDesktop().open(new File(System.getProperty("user.home")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAddVm() {
        if (addServerStage.isShowing()) {
            addServerStage.hide();
        } else {
            addServerStage.show();
        }
    }

    @FXML
    public void removeVm() {
        boolean confirmed = ConfirmationAlertFactory.createAndShow(
            "Do you really want to remove this backup?",
            "Watchdog"
        );

        if (confirmed) {
            BackupDefinition backupDefinitionToRemove = backupsTableView.getSelectionModel().getSelectedItem();
            backupDefinitionService.remove(backupDefinitionToRemove);
        }
    }

    @FXML
    public void modifyVm() {
        if (modifyVmStage.isShowing()) {
            modifyVmBackupController.clear();
            modifyVmStage.hide();
        } else {
            BackupDefinition backupDefinition = backupsTableView.getSelectionModel().getSelectedItem();

            modifyVmBackupController.prepareFor(backupDefinition);
            modifyVmStage.show();
        }
    }
}
