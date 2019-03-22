package com.github.tpiskorski.vboxcm.ui.controller;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import com.github.tpiskorski.vboxcm.vm.ConnectivityService;
import com.github.tpiskorski.vboxcm.vm.ServerMonitoringService;
import com.github.tpiskorski.vboxcm.vm.MonitorJob;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
    private final WorkbenchController workbenchController;
    private final ServerMonitoringService serverMonitoringService;

    @FXML private Alert serverExistsAlert;
    @FXML private Alert noConnectivityServerAlert;
    @FXML private Alert cancelledServerAlert;

    @FXML private RadioButton remoteRadioButton;
    @FXML private RadioButton localhostRadioButton;
    @FXML private ToggleGroup serversToggleGroup;

    @FXML private StackPane addServerStackPane;
    @FXML private GridPane addServerGridPane;
    @FXML private VBox progressLayer;

    @FXML private Button addButton;
    @FXML private Button closeButton;

    @FXML private TextField address;
    @FXML private TextField port;

    private String savedAddress;
    private String savedPort;

    private ConnectivityService connectivityService = new ConnectivityService();

    @Autowired
    public AddServerController(ServerService serverService, WorkbenchController workbenchController, ServerMonitoringService serverMonitoringService) {
        this.serverService = serverService;
        this.workbenchController = workbenchController;
        this.serverMonitoringService = serverMonitoringService;
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

        serversToggleGroup.selectedToggleProperty().addListener((observable, previousToggle, nextToggle) -> {
            if (nextToggle == localhostRadioButton) {
                savedAddress = address.getText();
                address.setText("localhost");
                savedPort = port.getText();
                port.clear();
            } else {
                address.setText(savedAddress);
                port.setText(savedPort);
            }
        });
    }

    @FXML
    public void saveConfig() {
        addServerGridPane.getScene().getWindow().setOnHiding(event -> {
            if (connectivityService.isRunning()) {
                connectivityService.cancel();
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

        connectivityService.setOnCancelled(workerStateEvent -> {
            cancelledServerAlert.showAndWait();
            closeWindow();
        });

        connectivityService.setOnFailed(workerStateEvent -> {
            noConnectivityServerAlert.showAndWait();
            closeWindow();
        });

        connectivityService.setOnSucceeded(workerStateEvent -> {
            serverService.add(server);
            serverMonitoringService.scheduleScan(server);
            closeWindow();
        });

        connectivityService.start();
    }

    private void closeWindow() {
        addServerStackPane.getChildren().remove(progressLayer);

        addServerGridPane.setDisable(false);
        workbenchController.enableMainWindow();

        address.clear();
        port.clear();
        remoteRadioButton.setSelected(true);

        connectivityService.reset();

        ((Stage) addButton.getScene().getWindow()).close();
    }

    @FXML
    private void closeButtonAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}