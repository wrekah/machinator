package com.github.tpiskorski.vboxcm.ui.controller.config;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ConfigViewController {

    private final ChangeConfigController changeConfigController;

    @FXML private GridPane changeConfig;
    @FXML private BorderPane settingsContainer;
    @FXML private ListView<String> settingsMenu;

    @Autowired public ConfigViewController(ChangeConfigController changeConfigController) {
        this.changeConfigController = changeConfigController;
    }

    public void initialize() {
        ObservableList<String> settings = FXCollections.observableArrayList("general");
        settingsMenu.setItems(settings);
        settingsMenu.getSelectionModel().select(0);
        changeConfig.setVisible(true);
        changeConfigController.reload();
        settingsContainer.setRight(changeConfig);

        ChangeListener<String> stringChangeListener = settingsSelectedTabListener();
        settingsMenu.getSelectionModel().selectedItemProperty().addListener(stringChangeListener);
    }

    private ChangeListener<String> settingsSelectedTabListener() {
        return (observable, oldValue, newValue) -> {
            if (observable.getValue() == "general") {
                changeConfig.setVisible(true);
                changeConfigController.reload();

                settingsContainer.setRight(changeConfig);
            }
        };
    }
}
