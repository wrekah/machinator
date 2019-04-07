package tpiskorski.machinator.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.springframework.stereotype.Controller;

@Controller
public class MainController {

    @FXML private BorderPane mainPane;

    public void disableMainWindow() {
        mainPane.setDisable(true);
    }

    public void enableMainWindow() {
        mainPane.setDisable(false);
    }
}
