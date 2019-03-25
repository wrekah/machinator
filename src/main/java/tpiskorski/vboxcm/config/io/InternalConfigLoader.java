package tpiskorski.vboxcm.config.io;

import tpiskorski.vboxcm.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InternalConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalConfigLoader.class);

    private final ConfigReader configReader;

    @Value("${config.internal.file.path}") private String internalConfigFilePath;

    @Autowired public InternalConfigLoader(ConfigReader configReader) {
        this.configReader = configReader;
    }

    public Config loadInternalConfig() {
        LOGGER.info("Going to read internal config {}", internalConfigFilePath);
        return configReader.read(internalConfigFilePath);
    }
}
