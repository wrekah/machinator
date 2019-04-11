package tpiskorski.machinator.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.machinator.config.ConfigService;
import tpiskorski.machinator.lifecycle.ShutdownService;
import tpiskorski.machinator.quartz.server.ServerRefresh;
import tpiskorski.machinator.ui.core.ContextAwareSceneLoader;

import java.io.IOException;

@Controller
public class FileMenuController {

    private final ShutdownService shutdownService;
    private final ContextAwareSceneLoader contextAwareSceneLoader;
    private final ConfigService configService;
    private final ServerRefresh serverRefresh;

    @FXML private MenuItem monitoringMenuItem;

    @FXML private Alert reloadAlert;
    @FXML private Alert monitorAlert;

    private Stage containerWindow;

    @Autowired
    public FileMenuController(ConfigService configService, ContextAwareSceneLoader contextAwareSceneLoader, ServerRefresh serverRefresh, ShutdownService shutdownService) {
        this.configService = configService;
        this.contextAwareSceneLoader = contextAwareSceneLoader;
        this.serverRefresh = serverRefresh;
        this.shutdownService = shutdownService;
    }

    @FXML
    public void initialize() throws IOException {
        containerWindow = contextAwareSceneLoader.loadPopup("/fxml/menu/config/config/baseConfigContainer.fxml");
        monitoringMenuItem.setText(serverRefresh.isPaused() ? "Start Monitoring" : "Stop Monitoring");
    }

    @FXML
    public void reloadConfig() {
        configService.reload();
        reloadAlert.showAndWait();
    }

    @FXML
    public void configWindow() {
        if (containerWindow.isShowing()) {
            containerWindow.hide();
        } else {
            containerWindow.show();
        }
    }

    @FXML
    public void exit() {
        shutdownService.shutdown();
    }

    @FXML
    public void freezeMonitoring() {
        if (serverRefresh.isPaused()) {
            serverRefresh.resume();
            monitoringMenuItem.setText("Stop Monitoring");
        } else {
            serverRefresh.pause();
            monitoringMenuItem.setText("Start Monitoring");
        }
        monitorAlert.showAndWait();
    }
}
