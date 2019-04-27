package tpiskorski.machinator.ui.controller.watchdog;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerService;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineService;
import tpiskorski.machinator.model.watchdog.Watchdog;
import tpiskorski.machinator.model.watchdog.WatchdogService;

@Controller
public class AddVmWatchdogController {

    private final WatchdogService watchdogService;

    @FXML private ComboBox<Server> serverComboBox;
    @FXML private ComboBox<VirtualMachine> vmComboBox;
    @FXML private ComboBox<Server> backupServerComboBox;

    @FXML private Button addButton;
    @FXML private Button cancelButton;

    @FXML private Alert watchdogExistsAlert;

    @Autowired private ServerService serverService;
    @Autowired private VirtualMachineService virtualMachineService;

    @Autowired public AddVmWatchdogController(WatchdogService watchdogService) {
        this.watchdogService = watchdogService;
    }

    @FXML
    public void initialize() {
        vmComboBox.disableProperty().bind(serverComboBox.valueProperty().isNull());
        backupServerComboBox.disableProperty().bind(vmComboBox.valueProperty().isNull());

        addButton.disableProperty().bind(
            Bindings.isNull(serverComboBox.valueProperty())
                .or(Bindings.isNull(vmComboBox.valueProperty()))
                .or(Bindings.isNull(backupServerComboBox.valueProperty()))
        );

        serverComboBox.pressedProperty().addListener((observable, oldValue, newValue) -> {
            serverComboBox.setItems(FXCollections.observableArrayList(serverService.getServers()));
        });

        vmComboBox.pressedProperty().addListener((observable, oldValue, newValue) -> {
            Server server = serverComboBox.getSelectionModel().getSelectedItem();
            vmComboBox.setItems(FXCollections.observableArrayList(virtualMachineService.getVms(server)));
        });

        backupServerComboBox.pressedProperty().addListener((observable, oldValue, newValue) -> {
            Server server = serverComboBox.getSelectionModel().getSelectedItem();
            ObservableList<Server> toDisplay = FXCollections.observableArrayList(serverService.getServers());
            toDisplay.remove(server);
            backupServerComboBox.setItems(toDisplay);
        });
    }

    @FXML
    public void add() {
        VirtualMachine vm = vmComboBox.getSelectionModel().getSelectedItem();
        Server backupServer = backupServerComboBox.getSelectionModel().getSelectedItem();

        Watchdog watchdog = new Watchdog(vm, backupServer);
        if (watchdogService.contains(watchdog)) {
            watchdogExistsAlert.showAndWait();
            ((Stage) addButton.getScene().getWindow()).close();
            clear();
            return;
        }
        watchdogService.add(watchdog);

        ((Stage) addButton.getScene().getWindow()).close();
        clear();
    }

    @FXML
    public void close() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        clear();
    }

    private void clear() {
        backupServerComboBox.getSelectionModel().clearSelection();
        vmComboBox.getSelectionModel().clearSelection();
        serverComboBox.getSelectionModel().clearSelection();
    }
}
