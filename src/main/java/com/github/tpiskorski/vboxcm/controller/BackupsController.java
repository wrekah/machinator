package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.BackupableVirtualMachine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class BackupsController {

    ObservableList<BackupableVirtualMachine> vms = FXCollections.observableArrayList();

    public TableView<BackupableVirtualMachine> backupsTableView;

    @FXML
    public void initialize(){
        BackupableVirtualMachine vm = new BackupableVirtualMachine();
        vm.setFileLimit(3);
        vm.setFrequency(10);
        vm.setBackupTime(LocalTime.of(12, 0));
        vm.setFirstBackupDay(LocalDate.of(2019, 1, 1));

        vms.add(vm);
        backupsTableView.setItems(vms);
    }
}
