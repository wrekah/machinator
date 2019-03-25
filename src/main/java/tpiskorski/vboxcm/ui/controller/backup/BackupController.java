package tpiskorski.vboxcm.ui.controller.backup;

import tpiskorski.vboxcm.config.Config;
import tpiskorski.vboxcm.config.ConfigService;
import tpiskorski.vboxcm.core.backup.Backup;
import tpiskorski.vboxcm.core.backup.BackupService;
import tpiskorski.vboxcm.ui.core.ContextAwareSceneLoader;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Controller
public class BackupController {

    public TableView<Backup> backupsTableView;
    public Button removeVmButton;
    public Button modifyButton;

    @FXML private TextField backupLocation;
    @Autowired private ConfigService configService;

    @Autowired private ContextAwareSceneLoader contextAwareSceneLoader;
    @FXML private TableColumn viewColumn;
    @Autowired private BackupService backupService;

    private Stage addServerStage;
    private Stage modifyVmStage;

    @Autowired private ModifyVmBackupController modifyVmBackupController;

    @FXML
    public void initialize() throws IOException {
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

        Callback<TableColumn<Backup, Void>, TableCell<Backup, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Backup, Void> call(final TableColumn<Backup, Void> param) {
                final TableCell<Backup, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("View");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Backup data = getTableView().getItems().get(getIndex());
                            browseHomeDirectory();
                            System.out.println("selectedData: " + data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                            setAlignment(Pos.CENTER);
                        }
                    }
                };
                return cell;
            }
        };

        viewColumn.setCellFactory(cellFactory);
        backupsTableView.setItems(backupService.getBackups());
    }

    private void browseHomeDirectory() {
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
        Backup backupToRemove = backupsTableView.getSelectionModel().getSelectedItem();
        backupService.remove(backupToRemove);
    }

    @FXML
    public void modify() {
        if (modifyVmStage.isShowing()) {
            modifyVmBackupController.clear();
            modifyVmStage.hide();
        } else {
            Backup backup = backupsTableView.getSelectionModel().getSelectedItem();

            modifyVmBackupController.prepareFor(backup);
            modifyVmStage.show();
        }
    }
}
