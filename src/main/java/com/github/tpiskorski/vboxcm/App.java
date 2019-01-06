package com.github.tpiskorski.vboxcm;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URL;

@SpringBootApplication
public class App extends javafx.application.Application {

    private ConfigurableApplicationContext springContext;
    private FXMLLoader fxmlLoader;
    private Parent rootNode;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        springContext.stop();
    }

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(App.class);
        fxmlLoader = new FXMLLoader();
        URL resource = getClass().getResource("/fxml/workbench.fxml");
        fxmlLoader.setLocation(resource);
        fxmlLoader.setControllerFactory(springContext::getBean);
    }
    @Override
    public void start(Stage stage) throws IOException {
        rootNode = fxmlLoader.load();

        stage.setTitle("Hello World");
        Scene scene = new Scene(rootNode);
        stage.setScene(scene);
        stage.show();;
    }

}