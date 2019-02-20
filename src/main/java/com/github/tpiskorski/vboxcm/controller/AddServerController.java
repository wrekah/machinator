package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.Server;
import com.github.tpiskorski.vboxcm.domain.ServerRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AddServerController {

    @FXML private  Button addButton;
    @FXML private  Button closeButton;

    @FXML private TextField address;
    @FXML private TextField port;

    @Autowired private ServerRepository serverRepository;


    public void saveConfig(   ) {
        System.out.println(address.getText());
        System.out.println(port.getText());

        serverRepository.add(new Server(address.getText()+":"+port.getText()));

        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();

    }

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
