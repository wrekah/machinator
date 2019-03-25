package tpiskorski.vboxcm.ui.controller.config;

import tpiskorski.vboxcm.config.Config;
import tpiskorski.vboxcm.config.ConfigService;
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
    @FXML private TextField sshUser;
    @FXML private TextField sshPassword;

    @Autowired public ChangeConfigController(ConfigService configService) {
        this.configService = configService;
    }

    public void reload() {
        Config config = configService.getConfig();

        pollInterval.setText("" + config.getPollInterval());
        backupLocation.setText(config.getBackupLocation());
        sshUser.setText(config.getSshUser());
        sshPassword.setText(config.getSshPassword());
    }

    public void saveConfig() {
        Config newConfig = Config.builder()
            .backupLocation(backupLocation.getText())
            .pollInterval(Integer.parseInt(pollInterval.getText()))
            .sshUser(sshUser.getText())
            .sshPassword(sshPassword.getText())
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
