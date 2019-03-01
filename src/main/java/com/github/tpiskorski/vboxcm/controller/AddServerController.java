package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.Server;
import com.github.tpiskorski.vboxcm.domain.ServerRepository;
import com.github.tpiskorski.vboxcm.stub.AddServerTask;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AddServerController {

    @FXML private VBox progressLayer;
    @FXML private GridPane inner;
    @FXML private StackPane addServerGridPane;
    @FXML private Button addButton;
    @FXML private Button closeButton;

    @FXML private TextField address;
    @FXML private TextField port;

    private final ServerRepository serverRepository;

    @Autowired
    public AddServerController(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @FXML
    public void initialize() {
        addButton.disableProperty().bind(
            Bindings.isEmpty(address.textProperty())
                .or(Bindings.isEmpty(port.textProperty()))
        );
    }

    public void saveConfig() {
        inner.setDisable(true);
        addServerGridPane.getChildren().add(progressLayer);

        Server server = new Server(address.getText() + ":" + port.getText());

        AddServerTask addServerTask = new AddServerTask(server);

        addServerTask.setOnSucceeded(workerStateEvent -> {
            addServerGridPane.getChildren().remove(progressLayer);

            inner.setDisable(false);
            Stage stage = (Stage) addButton.getScene().getWindow();
            serverRepository.add(server);

            address.clear();
            port.clear();

            stage.close();
        });

        new Thread(addServerTask).start();
    }

    @FXML
    private void closeButtonAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
