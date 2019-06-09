package tpiskorski.machinator.ui.controller.menu;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller
public class HelpMenuController {

    private final HostServices hostServices;

    @Value("${project.github.url") private String repositoryUrl;

    public HelpMenuController(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    public void openLinkToRepository() {
        hostServices.showDocument(repositoryUrl);
    }
}
