package tpiskorski.machinator.config.io;

import tpiskorski.machinator.config.Config;

import java.util.Properties;

public class PropertiesConfigConverter {
    public Config convert(Properties properties) {
        return Config.builder()
            .backupLocation(properties.getProperty("backup.location"))
            .pollInterval(Integer.parseInt(properties.getProperty("poll.interval")))
            .sshUser(properties.getProperty("ssh.user"))
            .sshPassword(properties.getProperty("ssh.password"))
            .build();
    }

    public Properties convert(Config config) {
        Properties properties = new Properties();

        properties.setProperty("backup.location", config.getBackupLocation());
        properties.setProperty("poll.interval", String.valueOf(config.getPollInterval()));
        properties.setProperty("ssh.user", config.getSshUser());
        properties.setProperty("ssh.password", config.getSshPassword());

        return properties;
    }
}
