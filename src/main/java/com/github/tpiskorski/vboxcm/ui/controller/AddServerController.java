package com.github.tpiskorski.vboxcm.ui.controller;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import com.github.tpiskorski.vboxcm.stub.dynamic.CheckConnectivityTask;
import com.github.tpiskorski.vboxcm.stub.dynamic.CheckConnectivityTaskFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AddServerController {

    private final ServerService serverService;
    private final CheckConnectivityTaskFactory checkConnectivityTaskFactory;
    private final WorkbenchController workbenchController;

    @FXML private RadioButton remoteRadioButton;
    @FXML private RadioButton localhostRadioButton;
    @FXML private ToggleGroup serversToggleGroup;

    private CheckConnectivityTask task;

    @FXML private StackPane addServerStackPane;
    @FXML private GridPane addServerGridPane;
    @FXML private VBox progressLayer;
    @FXML private Alert serverExistsAlert;

    @FXML private Button addButton;
    @FXML private Button closeButton;

    @FXML private TextField address;
    @FXML private TextField port;

    private String savedAddress;
    private String savedPort;

    @Autowired
    public AddServerController(ServerService serverService, CheckConnectivityTaskFactory checkConnectivityTaskFactory, WorkbenchController workbenchController) {
        this.serverService = serverService;
        this.checkConnectivityTaskFactory = checkConnectivityTaskFactory;
        this.workbenchController = workbenchController;
    }

    @FXML
    public void initialize() {
        address.disableProperty().bind(localhostRadioButton.selectedProperty());
        port.disableProperty().bind(localhostRadioButton.selectedProperty());

        BooleanBinding nonBlankAddress = Bindings.createBooleanBinding(() ->
                address.getText().trim().isEmpty(),
            address.textProperty()
        );

        BooleanBinding nonBlankPort = Bindings.createBooleanBinding(() ->
                port.getText().trim().isEmpty(),
            port.textProperty()
        );

        addButton.disableProperty().bind(nonBlankAddress.or(nonBlankPort).and(localhostRadioButton.selectedProperty().not()));

        serversToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle previousToggle, Toggle nextToggle) {
                if (nextToggle == localhostRadioButton) {
                    savedAddress = address.getText();
                    address.setText("localhost");
                    savedPort = port.getText();
                    port.clear();
                } else {
                    address.setText(savedAddress);
                    port.setText(savedPort);
                }
            }
        });
    }

    @FXML
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

        if (serverService.contains(server)) {
            serverExistsAlert.showAndWait();
            closeWindow();
            return;
        }

        task = checkConnectivityTaskFactory.taskFor(server);
        task.setOnCancelled(workerStateEvent -> closeWindow());
        task.setOnFailed(workerStateEvent -> closeWindow());
        task.setOnSucceeded(workerStateEvent -> {
            serverService.add(server);
            closeWindow();
        });

        new Thread(task).start();
    }

    private void closeWindow() {
        addServerStackPane.getChildren().remove(progressLayer);

        addServerGridPane.setDisable(false);
        workbenchController.enableMainWindow();
        Stage stage = (Stage) addButton.getScene().getWindow();

        address.clear();
        port.clear();
        remoteRadioButton.setSelected(true);

        stage.close();
    }

    @FXML
    private void closeButtonAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}