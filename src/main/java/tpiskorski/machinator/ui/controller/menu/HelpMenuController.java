package tpiskorski.machinator.ui.controller.menu;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.markdown4j.Markdown4jProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import tpiskorski.machinator.ui.core.ContextAwareSceneLoader;

import java.io.IOException;
import java.nio.file.Files;

@Controller
public class HelpMenuController {

    @Autowired private ContextAwareSceneLoader contextAwareSceneLoader;

    @Autowired private HelpViewerController helpViewerController;

    private Stage containerWindow;

    @FXML
    public void initialize() throws IOException {
        containerWindow = contextAwareSceneLoader.loadPopup("/fxml/menu/config/helpViewer.fxml");

        WebView webView = new WebView();
        ClassPathResource mainFxml = new ClassPathResource("README.md");
        byte[] buff = Files.readAllBytes(mainFxml.getFile().toPath());
        String content = new String(buff);

        webView.getEngine().loadContent(new Markdown4jProcessor().process(content));
        helpViewerController.setContent(webView);
    }

    @FXML
    public void displayHelp() {

        if (containerWindow.isShowing()) {
            containerWindow.hide();
        } else {
            containerWindow.show();
        }
    }
}
