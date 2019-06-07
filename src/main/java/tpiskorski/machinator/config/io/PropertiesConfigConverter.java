package tpiskorski.machinator.config.io;

import tpiskorski.machinator.config.Config;

import java.util.Properties;

public class PropertiesConfigConverter {
    public Config convert(Properties properties) {
        return Config.builder()
            .backupLocation(properties.getProperty("backup.location"))
            .pollInterval(Integer.parseInt(properties.getProperty("poll.interval")))
            .build();
    }

    public Properties convert(Config config) {
        Properties properties = new Properties();

        properties.setProperty("backup.location", config.getBackupLocation());
        properties.setProperty("poll.interval", String.valueOf(config.getPollInterval()));

        return properties;
    }
}
