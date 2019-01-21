package com.github.tpiskorski.vboxcm.config;

import com.github.tpiskorski.vboxcm.config.io.InternalConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
public class InternalConfigService implements ConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalConfigService.class);

    private final InternalConfigLoader internalConfigLoader;

    @Value("${config.internal.file.path}") String internalConfigFilePath;

    @Autowired public InternalConfigService(InternalConfigLoader internalConfigLoader) {
        this.internalConfigLoader = internalConfigLoader;
    }

    @Override public Config loadConfig() {
        LOGGER.info("Going to read internal config {}", internalConfigFilePath);
        return internalConfigLoader.loadInternalConfig();
    }

}
