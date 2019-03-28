package tpiskorski.vboxcm.ui.controller;

import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.server.ServerCoordinatingService;
import tpiskorski.vboxcm.core.server.ServerService;
import tpiskorski.vboxcm.monitoring.ConnectivityService;
import tpiskorski.vboxcm.monitoring.ServerMonitoringDaemon;
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
    private final ServerCoordinatingService serverCoordinatingService;
    private final WorkbenchController workbenchController;

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

    @Autowired private ConnectivityService connectivityService;

    @Autowired
    public AddServerController(ServerService serverService, WorkbenchController workbenchController, ServerMonitoringDaemon serverMonitoringDaemon, ServerCoordinatingService serverCoordinatingService) {
        this.serverService = serverService;
        this.workbenchController = workbenchController;
        this.serverCoordinatingService = serverCoordinatingService;
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
                address.setText("Local Machine");
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

        Server server = new Server(address.getText(), port.getText());

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
            serverCoordinatingService.add(server);
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