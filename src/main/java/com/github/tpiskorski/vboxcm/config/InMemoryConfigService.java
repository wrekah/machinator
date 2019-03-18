package com.github.tpiskorski.vboxcm.config;

import com.github.tpiskorski.vboxcm.config.io.InternalConfigLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
public class InMemoryConfigService extends ConfigService {

    private final InternalConfigLoader internalConfigLoader;

    @Autowired public InMemoryConfigService(InternalConfigLoader internalConfigLoader) {
        this.internalConfigLoader = internalConfigLoader;
    }

    @Override protected Config loadConfig() {
        return internalConfigLoader.loadInternalConfig();
    }

    @Override public void modifyConfig(Config newConfig) {
        firePropertyChange("configChange", config, newConfig);
        config = newConfig;
    }
}
