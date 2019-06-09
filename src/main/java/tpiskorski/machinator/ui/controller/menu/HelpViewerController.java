package tpiskorski.machinator.ui.controller.menu;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Controller;

@Controller
public class HelpViewerController {

    @FXML private ScrollPane scrollPane;

    void setContent(WebView webView) {
        scrollPane.setContent(webView);
    }
}
