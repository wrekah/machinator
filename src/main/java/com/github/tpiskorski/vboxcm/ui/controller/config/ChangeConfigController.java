package com.github.tpiskorski.vboxcm.ui.controller.config;

import com.github.tpiskorski.vboxcm.config.Config;
import com.github.tpiskorski.vboxcm.config.ConfigService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ChangeConfigController {

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
        //todo
    }
}
