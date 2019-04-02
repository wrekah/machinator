package tpiskorski.vboxcm.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.server.ServerService;
import tpiskorski.vboxcm.ui.control.ServerCellFactory;
import tpiskorski.vboxcm.ui.core.ContextAwareSceneLoader;

import java.io.IOException;
import java.util.Comparator;

@Controller
public class ServerController {

    @FXML private Button removeServerButton;
    @Autowired private ServerCellFactory serverCellFactory;

    @FXML private TextField filterField;
    @FXML private ListView<Server> serverList;

    @Autowired private ServerService serverService;
    @Autowired private ContextAwareSceneLoader contextAwareSceneLoader;
    @Autowired private VmController vmController;

    private Stage addServerStage;
    private Stage jobsStage;

    @FXML
    public void initialize() throws IOException {
        setupInputBindings();

        serverList.setCellFactory(serverCellFactory);
        removeServerButton.disableProperty().bind(Bindings.isEmpty(serverList.getSelectionModel().getSelectedItems()));

        addServerStage = contextAwareSceneLoader.load("/fxml/addServer.fxml");
        addServerStage.setTitle("Adding server...");

        jobsStage = contextAwareSceneLoader.load("/fxml/jobs.fxml");
        jobsStage.setTitle("Jobs");
        FilteredList<Server> filterableServers = new FilteredList<>(serverService.getServers(), p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterableServers.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                // Filter matches last name.
                if (person.getAddress().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else return person.getAddress().toLowerCase().contains(lowerCaseFilter);
            });
        });

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            vmController.getFilterableVirtualMachines().setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                // Filter matches last name.
                if (person.getServer().getSimpleAddress().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else return person.getServer().getSimpleAddress().contains(lowerCaseFilter);
            });
        });

        serverList.setItems(new SortedList<>(filterableServers, Comparator.comparing(Server::getServerType)));
    }

    private void setupInputBindings() {
        serverList.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                serverList.getSelectionModel().clearSelection();
            }
        });

        serverList.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                serverList.getSelectionModel().clearSelection();
            }
        });
    }

    @FXML
    public void removeServer() {
        Server serverToRemove = serverList.getSelectionModel().getSelectedItem();
        serverService.remove(serverToRemove);
    }

    @FXML
    public void addServer() {
        if (addServerStage.isShowing()) {
            addServerStage.hide();
        } else {
            addServerStage.show();
        }
    }

    @FXML
    public void showJobs(ActionEvent event) {
        if (jobsStage.isShowing()) {
            jobsStage.hide();
        } else {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            jobsStage.setX(currentStage.getX() + currentStage.getWidth());
            jobsStage.setY(currentStage.getY());
            jobsStage.show();
        }
    }
}
