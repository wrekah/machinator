package tpiskorski.machinator.ui.controller.watchdog;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.machinator.model.watchdog.Watchdog;
import tpiskorski.machinator.model.watchdog.WatchdogService;
import tpiskorski.machinator.ui.control.ConfirmationAlertFactory;
import tpiskorski.machinator.ui.control.WatchdogTableRow;
import tpiskorski.machinator.ui.core.ContextAwareSceneLoader;

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
    @FXML private Button dynamicButton;

    @Autowired
    public WatchdogController(ContextAwareSceneLoader contextAwareSceneLoader, WatchdogService watchdogService) {
        this.contextAwareSceneLoader = contextAwareSceneLoader;
        this.watchdogService = watchdogService;
    }

    @FXML
    public void initialize() throws IOException {
        watchdogTableView.setRowFactory((tableView) -> new WatchdogTableRow());
        watchdogTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                Watchdog selectedItem = watchdogTableView.getSelectionModel().getSelectedItem();
                if (selectedItem.isActive()) {
                    dynamicMenuItem.setText("Deactivate");
                    dynamicMenuItem.setOnAction(this::deactivate);
                } else {
                    dynamicMenuItem.setText("Activate");
                    dynamicMenuItem.setOnAction(this::activate);
                }

                contextMenu.show(watchdogTableView, t.getScreenX(), t.getScreenY());
            }
        });

        dynamicButton.setOnAction(event -> {
            Watchdog selectedItem = watchdogTableView.getSelectionModel().getSelectedItem();
            if (selectedItem.isActive()) {
                dynamicButton.setText("Deactivate");
                deactivate(event);
            } else {
                dynamicButton.setText("Activate");
                activate(event);
            }
        });

        addVmWatchdogStage = contextAwareSceneLoader.loadPopup("/fxml/watchdog/addVmWatchdog.fxml");
        addVmWatchdogStage.setTitle("Adding vm watchdog...");

        unwatchVmButton.disableProperty().bind(Bindings.isEmpty(watchdogTableView.getSelectionModel().getSelectedItems()));
        dynamicButton.disableProperty().bind(Bindings.isEmpty(watchdogTableView.getSelectionModel().getSelectedItems()));

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
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to unwatch this vm?",
            "Watchdog"
        );

        if (confirmed) {
            Watchdog watchdogToRemove = watchdogTableView.getSelectionModel().getSelectedItem();
            watchdogService.remove(watchdogToRemove);
        }
    }

    private void activate(ActionEvent actionEvent) {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to activate this watchdog?",
            "Watchdog"
        );

        if (confirmed) {
            Watchdog watchdogToActivate = watchdogTableView.getSelectionModel().getSelectedItem();
            watchdogService.activate(watchdogToActivate);
        }
    }

    private void deactivate(ActionEvent actionEvent) {
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to deactivate this watchdog?",
            "Watchdog"
        );

        if (confirmed) {
            Watchdog watchdogToActivate = watchdogTableView.getSelectionModel().getSelectedItem();
            watchdogService.deactivate(watchdogToActivate);
        }
    }
}
