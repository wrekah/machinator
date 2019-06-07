package tpiskorski.machinator.ui.controller.config;

import javafx.scene.control.CheckBox;
import tpiskorski.machinator.config.Config;
import tpiskorski.machinator.config.ConfigService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ChangeConfigController {

    @FXML private Button cancelButton;
    private ConfigService configService;

    @FXML private TextField pollInterval;
    @FXML private TextField backupLocation;
    @FXML private CheckBox notificationCheckbox;

    @Autowired public ChangeConfigController(ConfigService configService) {
        this.configService = configService;
    }

    public void reload() {
        Config config = configService.getConfig();

        pollInterval.setText("" + config.getPollInterval());
        backupLocation.setText(config.getBackupLocation());
        notificationCheckbox.setSelected(config.areNotificationsEnabled());
    }

    public void saveConfig() {
        Config newConfig = Config.builder()
            .backupLocation(backupLocation.getText())
            .pollInterval(Integer.parseInt(pollInterval.getText()))
            .notifications(notificationCheckbox.isSelected())
            .build();

        configService.modifyConfig(newConfig);
        close();
    }

    @FXML
    public void close() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
