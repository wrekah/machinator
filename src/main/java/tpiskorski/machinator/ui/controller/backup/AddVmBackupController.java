package tpiskorski.machinator.ui.controller.backup;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;
import tpiskorski.machinator.model.backup.BackupDefinition;
import tpiskorski.machinator.model.backup.BackupDefinitionService;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerService;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineService;

@DependsOn("mainController")
@Controller
public class AddVmBackupController {

    @Autowired private ServerService serverService;
    @Autowired private VirtualMachineService virtualMachineService;
    @Autowired private BackupDefinitionService backupDefinitionService;

    @FXML private TextField dayInterval;
    @FXML private TextField firstDay;
    @FXML private TextField backupHour;

    @FXML private TextField fileLimit;

    @FXML private ComboBox<Server> serverComboBox;
    @FXML private ComboBox<VirtualMachine> vmComboBox;

    @FXML private Button cancelButton;
    @FXML private Button addButton;

    @FXML
    public void initialize() {
        dayInterval.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                dayInterval.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        firstDay.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                firstDay.setText(newValue.replaceAll("[^\\d]", ""));
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

        BackupDefinition backupDefinition = new BackupDefinition(server, vm);

        backupDefinition.setStartAtDayOfTheMonth(Integer.parseInt(firstDay.getText()));
        backupDefinition.setRepeatInDays(Integer.parseInt(dayInterval.getText()));
        backupDefinition.setHour(Integer.parseInt(backupHour.getText()));
        backupDefinition.setFileLimit(Integer.parseInt(this.fileLimit.getText()));

        backupDefinitionService.add(backupDefinition);
        ((Stage) addButton.getScene().getWindow()).close();
        clear();
    }

    private void clear() {
        serverComboBox.getSelectionModel().clearSelection();
        vmComboBox.getSelectionModel().clearSelection();
        firstDay.clear();
        firstDay.clear();
        dayInterval.clear();
        fileLimit.clear();
    }
}
