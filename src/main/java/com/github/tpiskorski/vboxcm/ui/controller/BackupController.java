package com.github.tpiskorski.vboxcm.ui.controller;

import com.github.tpiskorski.vboxcm.core.backup.Backup;
import com.github.tpiskorski.vboxcm.core.backup.BackupService;
import com.github.tpiskorski.vboxcm.ui.core.ContextAwareSceneLoader;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class BackupController {

    public TableView<Backup> backupsTableView;
    public Button removeVmButton;

    @Autowired private ContextAwareSceneLoader contextAwareSceneLoader;
    @FXML private TableColumn viewColumn;
    @Autowired private BackupService backupService;

    @FXML
    public void initialize() throws IOException {
        removeVmButton.disableProperty().bind(Bindings.isEmpty(backupsTableView.getSelectionModel().getSelectedItems()));
        Callback<TableColumn<Backup, Void>, TableCell<Backup, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Backup, Void> call(final TableColumn<Backup, Void> param) {
                final TableCell<Backup, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("View");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Backup data = getTableView().getItems().get(getIndex());
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

    @FXML
    public void removeVm() {
        Backup backupToRemove = backupsTableView.getSelectionModel().getSelectedItem();
        backupService.remove(backupToRemove);
    }
}
