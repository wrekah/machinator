package com.github.tpiskorski.vboxcm.ui.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ContextAwareSceneLoader {

    private final ConfigurableApplicationContext springContext;

    @Autowired public ContextAwareSceneLoader(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    public Stage loadAndShow(String fxmlPath) throws IOException {
        Stage stage = new Stage();
        Scene scene = getScene(fxmlPath);

        stage.setResizable(false);
        stage.setScene(scene);

        stage.showAndWait();
        return stage;
    }

    public Stage load(String fxmlPath) throws IOException {
        Stage stage = new Stage();
        Scene scene = getScene(fxmlPath);

        stage.setResizable(false);
        stage.setScene(scene);

        return stage;
    }

    public Stage loadPopup(String fxmlPath) throws IOException {
        Stage stage = load(fxmlPath);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);
        return stage;
    }

    private Scene getScene(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        ClassPathResource mainFxml = new ClassPathResource(fxmlPath);
        fxmlLoader.setControllerFactory(springContext::getBean);
        fxmlLoader.setLocation(mainFxml.getURL());
        Parent rootNode = fxmlLoader.load();

        return new Scene(rootNode);
    }
}
