package tpiskorski.machinator.ui.controller.server;

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
import tpiskorski.machinator.flow.javafx.AddServerJavafxService;
import tpiskorski.machinator.model.server.Credentials;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerService;
import tpiskorski.machinator.ui.control.ExceptionDialog;
import tpiskorski.machinator.ui.controller.MainController;

@Controller
public class AddServerController {

    private static final String DEFAULT_SSH_PORT = "22";

    private final ServerService serverService;
    private final MainController mainController;
    private final AddServerJavafxService addServerJavafxService;

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
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;

    private String savedAddress;
    private String savedPort;
    private String savedUser;
    private String savedPassword;

    @Autowired
    public AddServerController(ServerService serverService, MainController mainController, AddServerJavafxService addServerJavafxService) {
        this.serverService = serverService;
        this.mainController = mainController;
        this.addServerJavafxService = addServerJavafxService;
    }

    @FXML
    public void initialize() {
        address.disableProperty().bind(localhostRadioButton.selectedProperty());
        port.disableProperty().bind(localhostRadioButton.selectedProperty());
        userField.disableProperty().bind(localhostRadioButton.selectedProperty());
        passwordField.disableProperty().bind(localhostRadioButton.selectedProperty());

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
                savedPort = port.getText();
                savedUser = userField.getText();
                savedPassword = passwordField.getText();

                address.setText("local");

                port.clear();
                userField.clear();
                passwordField.clear();
            } else {
                address.setText(savedAddress);
                port.setText(savedPort);
                userField.setText(savedUser);
                passwordField.setText(savedPassword);
            }
        });
    }

    @FXML
    public void addServer() {
        addServerGridPane.getScene().getWindow().setOnHiding(event -> {
            if (addServerJavafxService.isRunning()) {
                addServerJavafxService.cancel();
            }
        });

        addServerGridPane.setDisable(true);
        mainController.disableMainWindow();
        addServerStackPane.getChildren().add(progressLayer);

        Server server;
        if (serversToggleGroup.getSelectedToggle().isSelected()) {
            Credentials credentials = new Credentials(userField.getText(), passwordField.getText());
            server = new Server(credentials, address.getText(), port.getText());
        } else {
            server = new Server(address.getText(), port.getText());
        }

        if (serverService.contains(server)) {
            serverExistsAlert.showAndWait();
            closeWindow();
            return;
        }

        setServiceEventBindings();

        addServerJavafxService.start(server);
    }

    private void setServiceEventBindings() {
        addServerJavafxService.setOnCancelled(workerStateEvent -> {
            cancelledServerAlert.showAndWait();
            closeWindow();
        });

        addServerJavafxService.setOnFailed(workerStateEvent -> {
            ExceptionDialog exceptionDialog = new ExceptionDialog(addServerJavafxService.getException().toString());
            exceptionDialog.showAndWait();
            closeWindow();
        });

        addServerJavafxService.setOnSucceeded(workerStateEvent -> {
            closeWindow();
        });
    }

    private void closeWindow() {
        addServerStackPane.getChildren().remove(progressLayer);

        addServerGridPane.setDisable(false);
        mainController.enableMainWindow();
        resetFields();

        addServerJavafxService.reset();

        ((Stage) addButton.getScene().getWindow()).close();
    }

    @FXML
    private void cancelAddServer() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    void resetFields() {
        port.setText(DEFAULT_SSH_PORT);
        remoteRadioButton.setSelected(true);

        savedAddress = "";
        savedPort = "";
        savedUser = "";
        savedPassword = "";
    }
}