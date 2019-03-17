package com.github.tpiskorski.vboxcm.config.io;

import com.github.tpiskorski.vboxcm.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExternalConfigLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalConfigLoader.class);

    private final ConfigWriter configWriter;
    private final ConfigReader configReader;

    private FileChecker fileChecker = new FileChecker();

    @Value("${config.external.file.path}") private String externalConfigFilePath;

    @Autowired public ExternalConfigLoader(ConfigWriter configWriter, ConfigReader configReader) {
        this.configWriter = configWriter;
        this.configReader = configReader;
    }

    public Config loadExternalConfig() {
        LOGGER.info("Checking if external config {} exists...", externalConfigFilePath);
        if (fileChecker.notExists(externalConfigFilePath)) {
            LOGGER.info("External config not present. Creating default...");
            configWriter.write(externalConfigFilePath, Config.createDefault());
            LOGGER.info("Created default configuration -> {}", externalConfigFilePath);
        }

        LOGGER.info("Reading external config {}", externalConfigFilePath);
        return configReader.read(externalConfigFilePath);
    }

    public void saveConfig(Config config){
        configWriter.write(externalConfigFilePath, config);
    }
}
