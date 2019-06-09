package tpiskorski.machinator.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.config.io.InternalConfigLoader;

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
        Config old = Config.copy(config);
        config = newConfig;
        firePropertyChange("configChange", old, newConfig);
    }
}
