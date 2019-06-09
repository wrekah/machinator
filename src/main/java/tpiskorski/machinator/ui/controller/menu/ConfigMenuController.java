package tpiskorski.machinator.ui.controller.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.machinator.config.ConfigService;
import tpiskorski.machinator.ui.core.ContextAwareSceneLoader;

import java.io.IOException;

@Controller
public class ConfigMenuController {

    private final ContextAwareSceneLoader contextAwareSceneLoader;
    private final ConfigService configService;

    @FXML private Alert reloadAlert;

    private Stage containerWindow;

    @Autowired
    public ConfigMenuController(ContextAwareSceneLoader contextAwareSceneLoader, ConfigService configService) {
        this.contextAwareSceneLoader = contextAwareSceneLoader;
        this.configService = configService;
    }

    @FXML
    public void initialize() throws IOException {
        containerWindow = contextAwareSceneLoader.loadPopup("/fxml/menu/config/config/baseConfigContainer.fxml");
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
}
