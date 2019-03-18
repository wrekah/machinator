package com.github.tpiskorski.vboxcm.ui.controller.watchdog;

import com.github.tpiskorski.vboxcm.core.watchdog.Watchdog;
import com.github.tpiskorski.vboxcm.core.watchdog.WatchdogService;
import com.github.tpiskorski.vboxcm.ui.core.ContextAwareSceneLoader;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class WatchdogController {

    @FXML private TableView<Watchdog> watchdogTableView;
    @FXML private Button unwatchVmButton;

    @Autowired private WatchdogService watchdogService;
    @Autowired private ContextAwareSceneLoader contextAwareSceneLoader;

    private Stage addVmWatchdogStage;

    @FXML
    public void initialize() throws IOException {
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
