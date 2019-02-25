package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.*;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class WorkbenchController {

   @FXML private TableColumn vmNameColumn;

   @Autowired private ContextAwareSceneLoader contextAwareSceneLoader;

   @Autowired private ServerRepository serverRepository;
   @Autowired private VirtualMachineRepository virtualMachineRepository;
   @Autowired private JobRepository jobRepository;

   @FXML private Button removeServerButton;

   @FXML private Button removeVmButton;
   @FXML private Button resetVmButton;
   @FXML private Button powerOffVmButton;
   @FXML private Button turnOffVmButton;
   @FXML private Button turnOnVmButton;

   @FXML private TableView<VirtualMachine> virtualMachines;
   @FXML private TextField filterField;
   @FXML private ListView<Server> serverList;

   public void initialize() {


      removeVmButton.disableProperty().bind(Bindings.isEmpty(virtualMachines.getSelectionModel().getSelectedItems()));
      resetVmButton.disableProperty().bind(Bindings.isEmpty(virtualMachines.getSelectionModel().getSelectedItems()));
      powerOffVmButton.disableProperty().bind(Bindings.isEmpty(virtualMachines.getSelectionModel().getSelectedItems()));
      turnOffVmButton.disableProperty().bind(Bindings.isEmpty(virtualMachines.getSelectionModel().getSelectedItems()));
      turnOnVmButton.disableProperty().bind(Bindings.isEmpty(virtualMachines.getSelectionModel().getSelectedItems()));

      removeServerButton.disableProperty().bind(Bindings.isEmpty(serverList.getSelectionModel().getSelectedItems()));
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

      FilteredList<Server> filterableServers = new FilteredList<>(serverRepository.getServersList(), p -> true);
      FilteredList<VirtualMachine> filterableVirtualMachines = new FilteredList<>(virtualMachineRepository.getServersList(), p -> true);

      serverList.setCellFactory(new ServerCellFactory());
      filterField.textProperty().addListener((observable, oldValue, newValue) -> {
         filterableServers.setPredicate(person -> {
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

      filterField.textProperty().addListener((observable, oldValue, newValue) -> {
         filterableVirtualMachines.setPredicate(person -> {
            // If filter text is empty, display all persons.
            if (newValue == null || newValue.isEmpty()) {
               return true;
            }

            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = newValue.toLowerCase();

            // Filter matches last name.
            if (person.getServer() .toLowerCase().contains(lowerCaseFilter)) {
               return true; // Filter matches first name.
            } else return person.getServer() .toLowerCase().contains(lowerCaseFilter);
         });
      });

      serverList.setItems(filterableServers) ;
      virtualMachines.setItems(filterableVirtualMachines);
   }

   public void removeServer() {
      Server serverToRemove = serverList.getSelectionModel().getSelectedItem();
      serverRepository.remove(serverToRemove);
   }

   public void addServer() throws IOException {
      contextAwareSceneLoader.loadAndShow("/fxml/addServer.fxml");
   }

   public void showJobs() throws IOException {
      contextAwareSceneLoader.loadAndShow("/fxml/jobs.fxml");
   }

   public void turnOnVm() {
      VirtualMachine selectedItem = virtualMachines.getSelectionModel().getSelectedItem();
      Job job = new Job();
      job.setJobName("Turn on vm: "+selectedItem.getVmName());
      job.setProgress("Started");
      job.setStartTime(LocalDateTime.now());
      job.setStatus("In progress");
      jobRepository.add(job);
   }
}
