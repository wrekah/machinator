package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.Server;
import com.github.tpiskorski.vboxcm.domain.ServerRepository;
import com.github.tpiskorski.vboxcm.domain.VirtualMachine;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class WorkbenchController {

    @FXML
    private Button removeServerButton;
    @Autowired private ConfigurableApplicationContext springContext;

    @FXML
    private TableView<VirtualMachine> virtualMachines;
    @FXML
    private TextField filterField;
    @FXML
    private ListView<Server> serverList;

    @Autowired private ServerRepository serverRepository;

    private ObservableList<Server> observableList = FXCollections.observableArrayList();

    public void initialize() {
        removeServerButton.disableProperty().bind(Bindings.isEmpty(serverList.getSelectionModel().getSelectedItems()));
        serverList.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                serverList.getSelectionModel().clearSelection();
            }

        });

        FilteredList<Server> filteredData = new FilteredList<>(serverRepository.getServersList(), p -> true);

        serverList.setCellFactory(new ServerCellFactory());
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                // Filter matches last name.
                if (person.getAddress().get().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else return person.getAddress().get().toLowerCase().contains(lowerCaseFilter);
            });
        });

        serverList.setItems(filteredData);

    }


    public void removeServer() {
        Server serverToRemove = serverList.getSelectionModel().getSelectedItem();
        serverRepository.remove(serverToRemove);

    }

    public void addServer() throws IOException {
        Stage stage = new Stage();
        stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader();
        ClassPathResource mainFxml = new ClassPathResource("/fxml/addServer.fxml");
        fxmlLoader.setControllerFactory(springContext::getBean);
        fxmlLoader.setLocation(mainFxml.getURL());
        Parent rootNode = fxmlLoader.load();

        Scene scene = new Scene(rootNode);
        stage.setScene(scene);

        stage.showAndWait();

    }

}
