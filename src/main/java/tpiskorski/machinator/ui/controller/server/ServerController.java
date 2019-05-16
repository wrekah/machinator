package tpiskorski.machinator.ui.controller.server;

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
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerService;
import tpiskorski.machinator.ui.control.ConfirmationAlertFactory;
import tpiskorski.machinator.ui.control.ServerCellFactory;
import tpiskorski.machinator.ui.controller.vm.VmController;
import tpiskorski.machinator.ui.core.ContextAwareSceneLoader;

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
    @Autowired private AddServerController addServerController;

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

        filterField.textProperty().addListener((observable, previousSearchString, nextSearchString) -> {
            filterableServers.setPredicate(server -> {
                if (nextSearchString == null || nextSearchString.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = nextSearchString.toLowerCase();
                String lowerCaseAddress = server.getAddress().toLowerCase();

                if (lowerCaseAddress.contains(lowerCaseFilter)) {
                    return true;
                } else {
                    return lowerCaseAddress.contains(lowerCaseFilter);
                }
            });
        });

        filterField.textProperty().addListener((observable, previousSearchString, nextSearchString) -> {
            vmController.getFilterableVirtualMachines().setPredicate(vm -> {
                if (nextSearchString == null || nextSearchString.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = nextSearchString.toLowerCase();
                String lowerCaseAddress = vm.getServer().getSimpleAddress().toLowerCase();

                if (lowerCaseAddress.contains(lowerCaseFilter)) {
                    return true;
                } else {
                    return lowerCaseAddress.contains(lowerCaseFilter);
                }
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
        boolean confirmed = ConfirmationAlertFactory.createAndAsk(
            "Do you really want to remove this server?",
            "Server"
        );

        if (confirmed) {
            Server serverToRemove = serverList.getSelectionModel().getSelectedItem();
            serverService.remove(serverToRemove);
        }
    }

    @FXML
    public void addServer() {
        addServerController.resetFields();
        if (addServerStage.isShowing()) {
            addServerStage.hide();
        } else {
            addServerStage.show();
        }
        addServerController.resetFields();
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
