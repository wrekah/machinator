package tpiskorski.machinator.config;

import tpiskorski.machinator.config.io.ExternalConfigLoader;
import tpiskorski.machinator.config.io.InternalConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("!dev")
@Service
public class ExternalDefaultingConfigService extends ConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalDefaultingConfigService.class);

    private final ExternalConfigLoader externalConfigLoader;
    private final InternalConfigLoader internalConfigLoader;

    @Autowired
    public ExternalDefaultingConfigService(ExternalConfigLoader externalConfigLoader, InternalConfigLoader internalConfigLoader) {
        this.externalConfigLoader = externalConfigLoader;
        this.internalConfigLoader = internalConfigLoader;
    }

    @Override
    protected Config loadConfig() {
        try {
            return externalConfigLoader.loadExternalConfig();
        } catch (Exception e) {
            LOGGER.warn("Could not read or create external config.", e);
            LOGGER.warn("Defaulting to internal config...");
            return internalConfigLoader.loadInternalConfig();
        }
    }

    @Override public void modifyConfig(Config newConfig) {
        firePropertyChange("configChange", config, newConfig);
        externalConfigLoader.saveConfig(newConfig);
    }
}
