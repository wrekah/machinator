package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.Server;
import com.github.tpiskorski.vboxcm.domain.ServerRepository;
import com.github.tpiskorski.vboxcm.stub.AddServerTask;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AddServerController {

   @FXML private GridPane inner;
   @FXML private StackPane addServerGridPane;
   @FXML private Button addButton;
   @FXML private Button closeButton;

   @FXML private TextField address;
   @FXML private TextField port;

   @Autowired private ServerRepository serverRepository;

   public void initialize() {
      addButton.disableProperty().bind(
         Bindings.isEmpty(address.textProperty())
            .or(Bindings.isEmpty(port.textProperty()))
      );
   }

   public void saveConfig() throws InterruptedException {
      ProgressIndicator pi = new ProgressIndicator();
      VBox vbox1 = new VBox(pi);
      Label label = new Label("Adding...");
      VBox vbox2 = new VBox(label);

      vbox1.getChildren().add(label);
      vbox2.getChildren().add(label);
      vbox1.setAlignment(Pos.CENTER);
      vbox2.setAlignment(Pos.CENTER);

      inner.setDisable(true);
      addServerGridPane.getChildren().add(vbox2);

      addServerGridPane.getChildren().add(vbox1);

      Server server = new Server(address.getText() + ":" + port.getText());

      AddServerTask addServerTask = new AddServerTask(server);

      addServerTask.setOnSucceeded(workerStateEvent -> {
         addServerGridPane.getChildren().remove(vbox1);
         addServerGridPane.getChildren().remove(vbox2);
         inner.setDisable(false);
         Stage stage = (Stage) addButton.getScene().getWindow();
         serverRepository.add(server);

         stage.close();
      });

      new Thread(addServerTask).start();
   }

   @FXML
   private void closeButtonAction() {
      Stage stage = (Stage) closeButton.getScene().getWindow();
      stage.close();
   }
}
