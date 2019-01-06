package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.Server;
import com.github.tpiskorski.vboxcm.domain.VirtualMachine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Controller;

import java.util.stream.IntStream;

@Controller
public class WorkbenchController {


    @FXML
    private TableView<VirtualMachine> virtualMachines;
    @FXML
    private TextField filterField;
    @FXML
    private ListView<Server> serverList;
    private ObservableList<Server> observableList = FXCollections.observableArrayList();

    public void initialize() {
        IntStream.range(0, 1000).forEach(num -> observableList.add(new Server("localhost:" + num)));
        FilteredList<Server> filteredData = new FilteredList<>(observableList, p -> true);

        serverList.setCellFactory(new ServerCellFactory());
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (person.getAddress().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (person.getAddress().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        serverList.setItems(filteredData);

    }


}
