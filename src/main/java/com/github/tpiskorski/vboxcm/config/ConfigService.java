package com.github.tpiskorski.vboxcm.config;

public abstract class ConfigService {

    protected Config config;

    protected abstract Config loadConfig();
    public abstract void modifyConfig(Config newConfig);

    public void reload() {
        config = loadConfig();
    }

    public Config getConfig() {
        if (config == null) {
            config = loadConfig();
            return config;
        } else {
            return config;
        }
    }
}
