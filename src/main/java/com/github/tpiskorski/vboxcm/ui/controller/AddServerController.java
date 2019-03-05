package com.github.tpiskorski.vboxcm.ui.controller;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.stub.dynamic.AddServerTask;
import com.github.tpiskorski.vboxcm.stub.dynamic.AddServerTaskFactory;
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

@Controller
public class AddServerController {

    private final AddServerTaskFactory addServerTaskFactory;
    private final WorkbenchController workbenchController;

    private AddServerTask task;

    @FXML private StackPane addServerStackPane;
    @FXML private GridPane addServerGridPane;
    @FXML private VBox progressLayer;

    @FXML private Button addButton;
    @FXML private Button closeButton;

    @FXML private TextField address;
    @FXML private TextField port;

    @Autowired
    public AddServerController(AddServerTaskFactory addServerTaskFactory, WorkbenchController workbenchController) {
        this.addServerTaskFactory = addServerTaskFactory;
        this.workbenchController = workbenchController;
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
        addServerGridPane.getScene().getWindow().setOnHiding(event -> {
            if (task != null) {
                task.cancel();
            }
        });

        addServerGridPane.setDisable(true);
        workbenchController.disableMainWindow();
        addServerStackPane.getChildren().add(progressLayer);

        Server server = new Server(address.getText() + ":" + port.getText());

        task = addServerTaskFactory.taskFor(server);
        task.setOnCancelled(workerStateEvent -> closeWindow());
        task.setOnFailed(workerStateEvent -> closeWindow());
        task.setOnSucceeded(workerStateEvent -> closeWindow());

        new Thread(task).start();
    }

    private void closeWindow() {
        addServerStackPane.getChildren().remove(progressLayer);

        addServerGridPane.setDisable(false);
        workbenchController.enableMainWindow();
        Stage stage = (Stage) addButton.getScene().getWindow();

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