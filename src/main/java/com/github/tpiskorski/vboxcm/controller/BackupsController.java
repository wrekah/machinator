package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.BackupableVirtualMachine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class BackupsController {

    public TableView<BackupableVirtualMachine> backupsTableView;
    ObservableList<BackupableVirtualMachine> vms = FXCollections.observableArrayList();
    @FXML private TableColumn viewColumn;

    @FXML
    public void initialize() {

        Callback<TableColumn<BackupableVirtualMachine, Void>, TableCell<BackupableVirtualMachine, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<BackupableVirtualMachine, Void> call(final TableColumn<BackupableVirtualMachine, Void> param) {
                final TableCell<BackupableVirtualMachine, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("View");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            BackupableVirtualMachine data = getTableView().getItems().get(getIndex());
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

        BackupableVirtualMachine vm = new BackupableVirtualMachine();
        vm.setServer("localhost:22");
        vm.setVm("working-vm");
        vm.setFileLimit(3);
        vm.setFrequency(10);
        vm.setBackupTime(LocalTime.of(12, 0));
        vm.setFirstBackupDay(LocalDate.of(2019, 1, 1));

        vms.add(vm);
        backupsTableView.setItems(vms);
    }
}
