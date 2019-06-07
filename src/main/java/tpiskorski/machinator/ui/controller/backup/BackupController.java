package tpiskorski.machinator.ui.controller.backup;

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
import tpiskorski.machinator.config.Config;
import tpiskorski.machinator.config.ConfigService;
import tpiskorski.machinator.model.backup.BackupDefinition;
import tpiskorski.machinator.model.backup.BackupDefinitionService;
import tpiskorski.machinator.ui.control.BackupTableRow;
import tpiskorski.machinator.ui.control.ConfirmationAlertFactory;
import tpiskorski.machinator.ui.core.ContextAwareSceneLoader;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Controller
public class BackupController {

    @Autowired private ConfigService configService;
    @Autowired private ContextAwareSceneLoader contextAwareSceneLoader;
    @Autowired private BackupDefinitionService backupDefinitionService;

    @FXML private TableView<BackupDefinition> backupsTableView;
    @FXML private Button removeVmButton;
    @FXML private TextField backupLocation;
    @FXML private ContextMenu contextMenu;
    @FXML private MenuItem dynamicMenuItem;
    @FXML private Button triggerNowButton;

    private Stage addServerStage;

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
        backupsTableView.setRowFactory((tableview) -> new BackupTableRow());

        backupLocation.setText(configService.getConfig().getBackupLocation());
        configService.addPropertyChangeListener(evt -> {
            Config newValue = (Config) evt.getNewValue();
            backupLocation.setText(newValue.getBackupLocation());
        });

        removeVmButton.disableProperty().bind(Bindings.isEmpty(backupsTableView.getSelectionModel().getSelectedItems()));
        triggerNowButton.disableProperty().bind(Bindings.isEmpty(backupsTableView.getSelectionModel().getSelectedItems()));

        addServerStage = contextAwareSceneLoader.loadPopup("/fxml/backup/addVmBackup.fxml");
        addServerStage.setTitle("Adding backup...");

        backupsTableView.setItems(backupDefinitionService.getBackups());
    }

    private void deactivate(ActionEvent actionEvent) {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to deactivate this backup?",
            "Backup"
        );

        if (confirmed) {
            BackupDefinition backupToDeactivate = backupsTableView.getSelectionModel().getSelectedItem();
            backupDefinitionService.deactivate(backupToDeactivate);
        }
    }

    private void activate(ActionEvent actionEvent) {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to activate this backup?",
            "Backup"
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
    public void removeBackup() {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to remove this backup?",
            "Backup"
        );

        if (confirmed) {
            BackupDefinition backupDefinitionToRemove = backupsTableView.getSelectionModel().getSelectedItem();
            backupDefinitionService.remove(backupDefinitionToRemove);
        }
    }

    @FXML
    public void triggerNow() {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to trigger this backup now?",
            "Backup"
        );

        if (confirmed) {
            BackupDefinition backupDefinitionToTrigger = backupsTableView.getSelectionModel().getSelectedItem();
            backupDefinitionService.triggerNow(backupDefinitionToTrigger);
        }
    }
}
