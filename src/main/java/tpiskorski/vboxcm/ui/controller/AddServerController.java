package tpiskorski.vboxcm.ui.controller;

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
import tpiskorski.vboxcm.core.server.AddServerService;
import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.server.ServerService;
import tpiskorski.vboxcm.ui.control.ExceptionDialog;

@Controller
public class AddServerController {

    private final ServerService serverService;
    private final MainController mainController;
    private final AddServerService addServerService;

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

    @Autowired
    public AddServerController(ServerService serverService, MainController mainController, AddServerService addServerService) {
        this.serverService = serverService;
        this.mainController = mainController;
        this.addServerService = addServerService;
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
    public void addServer() {
        addServerGridPane.getScene().getWindow().setOnHiding(event -> {
            if (addServerService.isRunning()) {
                addServerService.cancel();
            }
        });

        addServerGridPane.setDisable(true);
        mainController.disableMainWindow();
        addServerStackPane.getChildren().add(progressLayer);

        Server server = new Server(address.getText(), port.getText());

        if (serverService.contains(server)) {
            serverExistsAlert.showAndWait();
            closeWindow();
            return;
        }

        addServerService.setOnCancelled(workerStateEvent -> {
            cancelledServerAlert.showAndWait();
            closeWindow();
        });

        addServerService.setOnFailed(workerStateEvent -> {
            ExceptionDialog exceptionDialog = new ExceptionDialog(addServerService.getException().toString());
            exceptionDialog.showAndWait();
            closeWindow();
        });

        addServerService.setOnSucceeded(workerStateEvent -> {
            closeWindow();
        });

        addServerService.start(server);
    }

    private void closeWindow() {
        addServerStackPane.getChildren().remove(progressLayer);

        addServerGridPane.setDisable(false);
        mainController.enableMainWindow();

        address.clear();
        port.clear();
        remoteRadioButton.setSelected(true);

        addServerService.reset();

        ((Stage) addButton.getScene().getWindow()).close();
    }

    @FXML
    private void cancelAddServer() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}