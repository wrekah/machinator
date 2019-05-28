package tpiskorski.machinator.ui.control;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.config.ConfigService;

@Component
public class NotificationFactory {

    private final ConfigService configService;

    public NotificationFactory(ConfigService configService) {
        this.configService = configService;
    }

    public void createAndShow(String msg) {
        if (!configService.getConfig().areNotificationsEnabled()) {
            return;
        }

        Notifications.create()
            .position(Pos.TOP_RIGHT)
            .hideAfter(Duration.seconds(3))
            .title("Machinator")
            .text(msg)
            .show();
    }
}
