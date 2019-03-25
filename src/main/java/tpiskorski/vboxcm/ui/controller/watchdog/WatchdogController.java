package tpiskorski.vboxcm.ui.controller.watchdog;

import tpiskorski.vboxcm.core.watchdog.Watchdog;
import tpiskorski.vboxcm.core.watchdog.WatchdogService;
import tpiskorski.vboxcm.ui.control.WatchdogServerCellValueFactory;
import tpiskorski.vboxcm.ui.core.ContextAwareSceneLoader;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class WatchdogController {

    private final WatchdogService watchdogService;
    private final ContextAwareSceneLoader contextAwareSceneLoader;

    @FXML private TableColumn<Watchdog, String> serverTableColumn;
    @FXML private TableView<Watchdog> watchdogTableView;
    @FXML private Button unwatchVmButton;

    private Stage addVmWatchdogStage;

    @Autowired
    public WatchdogController(ContextAwareSceneLoader contextAwareSceneLoader, WatchdogService watchdogService) {
        this.contextAwareSceneLoader = contextAwareSceneLoader;
        this.watchdogService = watchdogService;
    }

    @FXML
    public void initialize() throws IOException {
        serverTableColumn.setCellValueFactory(new WatchdogServerCellValueFactory());

        Callback<TableColumn.CellDataFeatures<Watchdog, String>, ObservableValue<String>> cellDataFeaturesObservableValueCallback = p -> new SimpleStringProperty(p.getValue().getVirtualMachine().getServerAddress());

        serverTableColumn.setCellValueFactory(cellDataFeaturesObservableValueCallback);

        addVmWatchdogStage = contextAwareSceneLoader.loadPopup("/fxml/watchdog/addVmWatchdog.fxml");
        addVmWatchdogStage.setTitle("Adding vm watchdog...");

        unwatchVmButton.disableProperty().bind(Bindings.isEmpty(watchdogTableView.getSelectionModel().getSelectedItems()));

        watchdogTableView.setItems(watchdogService.getWatchdogs());
    }

    public void watchVm() {
        if (addVmWatchdogStage.isShowing()) {
            addVmWatchdogStage.hide();
        } else {
            addVmWatchdogStage.show();
        }
    }

    @FXML
    public void unwatchVm() {
        Watchdog watchdogToRemove = watchdogTableView.getSelectionModel().getSelectedItem();
        watchdogService.remove(watchdogToRemove);
    }
}
