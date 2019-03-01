package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.Server;
import com.github.tpiskorski.vboxcm.domain.ServerRepository;
import com.github.tpiskorski.vboxcm.stub.AddServerTask;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

interface Command {

}

@Controller
public class AddServerController {

    private final ServerRepository serverRepository;
    AddServerTask addServerTask;

    @Autowired private WorkbenchController workbenchController;

    @FXML private VBox progressLayer;
    @FXML private GridPane inner;
    @FXML private StackPane addServerGridPane;
    @FXML private Button addButton;
    @FXML private Button closeButton;
    @FXML private TextField address;
    @FXML private TextField port;

    @Autowired
    public AddServerController(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @FXML
    public void initialize() {
        BooleanBinding nonBlankAddress = Bindings.createBooleanBinding(() ->
                address.getText().trim().isEmpty(),
            address.textProperty()
        );

        BooleanBinding nonBlankPort = Bindings.createBooleanBinding(() ->
                port.getText().trim().isEmpty(),
            port.textProperty()
        );

        addButton.disableProperty().bind(nonBlankAddress.or(nonBlankPort));
    }

    public void saveConfig() {
        inner.getScene().getWindow().setOnHiding(event -> {
            if (addServerTask != null) {
                addServerTask.cancel();
            }
        });

        inner.setDisable(true);
        workbenchController.border.setDisable(true);
        addServerGridPane.getChildren().add(progressLayer);

        Server server = new Server(address.getText() + ":" + port.getText());

        addServerTask = new AddServerTask(server);

        addServerTask.setOnCancelled(workerStateEvent -> {
            closeWindow(server);
        });
        addServerTask.setOnFailed(workerStateEvent -> {
            closeWindow(server);
        });
        addServerTask.setOnSucceeded(workerStateEvent -> {
            closeWindow(server);
        });

        new Thread(addServerTask).start();
    }

    private void closeWindow(Server server) {
        addServerGridPane.getChildren().remove(progressLayer);

        inner.setDisable(false);
        workbenchController.border.setDisable(false);
        Stage stage = (Stage) addButton.getScene().getWindow();
        serverRepository.add(server);

        address.clear();
        port.clear();

        stage.close();
    }

    @FXML
    private void closeButtonAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}