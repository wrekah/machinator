package com.github.tpiskorski.vboxcm;

import com.github.tpiskorski.vboxcm.discovery.DiscoveryService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ServiceLoader;

public class App extends javafx.application.Application {

    @Override
    public void start(Stage stage) {
        ServiceLoader
        DiscoveryService discoveryService = new DiscoveryService();
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}