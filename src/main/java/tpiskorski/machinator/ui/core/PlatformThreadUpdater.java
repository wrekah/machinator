package tpiskorski.machinator.ui.core;

import javafx.application.Platform;
import org.springframework.stereotype.Service;

@Service
public class PlatformThreadUpdater {

    public void runLater(PlatformThreadAction platformThreadAction) {
        Platform.runLater(platformThreadAction::perform);
    }
}
