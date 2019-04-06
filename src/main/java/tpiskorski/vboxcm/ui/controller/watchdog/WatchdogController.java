package tpiskorski.vboxcm.ui.controller.watchdog;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.vboxcm.core.watchdog.Watchdog;
import tpiskorski.vboxcm.core.watchdog.WatchdogService;
import tpiskorski.vboxcm.ui.core.ContextAwareSceneLoader;

import java.io.IOException;

@Controller
public class WatchdogController {

    private final WatchdogService watchdogService;
    private final ContextAwareSceneLoader contextAwareSceneLoader;

    @FXML private TableView<Watchdog> watchdogTableView;
    @FXML private Button unwatchVmButton;

    private Stage addVmWatchdogStage;
    @FXML private ContextMenu contextMenu;
    @FXML private MenuItem dynamicMenuItem;

    @Autowired
    public WatchdogController(ContextAwareSceneLoader contextAwareSceneLoader, WatchdogService watchdogService) {
        this.contextAwareSceneLoader = contextAwareSceneLoader;
        this.watchdogService = watchdogService;
    }

    @FXML
    public void initialize() throws IOException {
        watchdogTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                Watchdog selectedItem = watchdogTableView.getSelectionModel().getSelectedItem();
                if (selectedItem.isActive()) {
                    dynamicMenuItem.setText(  "Deactivate" );
                    dynamicMenuItem.setOnAction(this::deactivate);
                }else{
                    dynamicMenuItem.setText(  "Activate" );
                    dynamicMenuItem.setOnAction(this::activate);
                }

                contextMenu.show(watchdogTableView, t.getScreenX(), t.getScreenY());
            }
        });

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

    private void activate(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Do you really want to activate this watchdog?",
            ButtonType.YES, ButtonType.NO
        );

        alert.setTitle("Watchdog");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            Watchdog watchdogToActivate = watchdogTableView.getSelectionModel().getSelectedItem();
            watchdogService.activate(watchdogToActivate);
        }
    }


    private void deactivate(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Do you really want to deactivate this watchdog?",
            ButtonType.YES, ButtonType.NO
        );

        alert.setTitle("Watchdog");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            Watchdog watchdogToActivate = watchdogTableView.getSelectionModel().getSelectedItem();
            watchdogService.deactivate(watchdogToActivate);
        }
    }
}
