package com.github.tpiskorski.vboxcm.controller.config;

import com.github.tpiskorski.vboxcm.config.ConfigService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class FileMenuController {

    private final ConfigService configService;
    @FXML private Alert reloadAlert;

    @Autowired private ConfigurableApplicationContext springContext;

    @Autowired public FileMenuController(ConfigService configService) {
        this.configService = configService;
    }

    public void reloadConfig() {
        configService.reload();
        reloadAlert.showAndWait();
    }

    public void modify() throws IOException {
        Stage stage = new Stage();
        stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader();
        ClassPathResource mainFxml = new ClassPathResource("/fxml/menu/config/settings/containerWindow.fxml");
        fxmlLoader.setControllerFactory(springContext::getBean);
        fxmlLoader.setLocation(mainFxml.getURL());
        Parent rootNode = fxmlLoader.load();

        Scene scene = new Scene(rootNode);
        stage.setScene(scene);
        stage.show();
    }

    public void exit() {
        springContext.close();
        Platform.exit();
    }
}
