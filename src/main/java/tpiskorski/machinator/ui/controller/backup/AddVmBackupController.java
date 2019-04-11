package tpiskorski.machinator.ui.controller.backup;

import javafx.collections.FXCollections;
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
import tpiskorski.machinator.core.backup.BackupDefinition;
import tpiskorski.machinator.core.backup.BackupDefinitionService;
import tpiskorski.machinator.core.server.Server;
import tpiskorski.machinator.core.server.ServerService;
import tpiskorski.machinator.core.vm.VirtualMachine;
import tpiskorski.machinator.core.vm.VirtualMachineService;

import java.time.LocalDate;
import java.time.LocalTime;

@DependsOn("mainController")
@Controller
public class AddVmBackupController {

    @Autowired private ServerService serverService;
    @Autowired private VirtualMachineService virtualMachineService;
    @Autowired private BackupDefinitionService backupDefinitionService;

    @FXML private TextField frequency;
    @FXML private Button cancelButton;
    @FXML private Button addButton;
    @FXML private DatePicker firstBackup;
    @FXML private TextField backupTime;
    @FXML private TextField fileLimit;
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

        serverComboBox.pressedProperty().addListener((observable, oldValue, newValue) -> {
            serverComboBox.setItems(FXCollections.observableArrayList(serverService.getServers()));
        });

        vmComboBox.pressedProperty().addListener((observable, oldValue, newValue) -> {
            Server server = serverComboBox.getSelectionModel().getSelectedItem();
            vmComboBox.setItems(FXCollections.observableArrayList(virtualMachineService.getVms(server)));
        });
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
        int every = Integer.parseInt(frequency.getText());
        LocalTime backupTime = LocalTime.parse(this.backupTime.getText());
        int fileLimit = Integer.parseInt(this.fileLimit.getText());

        BackupDefinition backupDefinition = new BackupDefinition(server, vm);

        backupDefinition.setFirstBackupDay(backupDay);
        backupDefinition.setFrequency(every);
        backupDefinition.setBackupTime(backupTime);
        backupDefinition.setFileLimit(fileLimit);

        backupDefinitionService.add(backupDefinition);
        ((Stage) addButton.getScene().getWindow()).close();
        clear();
    }

    private void clear() {
        serverComboBox.getSelectionModel().clearSelection();
        vmComboBox.getSelectionModel().clearSelection();
        firstBackup.getEditor().clear();
        frequency.clear();
        backupTime.clear();
        fileLimit.clear();
    }
}
