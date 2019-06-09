package tpiskorski.machinator.ui.controller.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.machinator.flow.quartz.server.ServerRefreshScheduler;
import tpiskorski.machinator.lifecycle.ShutdownService;

@Controller
public class FileMenuController {

    private final ShutdownService shutdownService;
    private final ServerRefreshScheduler serverRefreshScheduler;

    @FXML private MenuItem monitoringMenuItem;

    @FXML private Alert monitorAlert;

    @Autowired
    public FileMenuController(ServerRefreshScheduler serverRefreshScheduler, ShutdownService shutdownService) {
        this.serverRefreshScheduler = serverRefreshScheduler;
        this.shutdownService = shutdownService;
    }

    @FXML
    public void initialize() {
        monitoringMenuItem.setText(serverRefreshScheduler.isPaused() ? "Start Monitoring" : "Stop Monitoring");
    }

    @FXML
    public void exit() {
        shutdownService.shutdown();
    }

    @FXML
    public void freezeMonitoring() {
        if (serverRefreshScheduler.isPaused()) {
            serverRefreshScheduler.resume();
            monitoringMenuItem.setText("Stop Monitoring");
        } else {
            serverRefreshScheduler.pause();
            monitoringMenuItem.setText("Start Monitoring");
        }
        monitorAlert.showAndWait();
    }
}
