package com.github.tpiskorski.vboxcm.config.io;

import com.github.tpiskorski.vboxcm.config.Config;

import java.util.Properties;

public class PropertiesConfigConverter {
    public Config convert(Properties properties) {
        Config config = new Config();

        config.setBackupLocation(properties.getProperty("backup.location"));
        config.setPollInterval(Integer.parseInt(properties.getProperty("poll.interval")));
        config.setSshUser(properties.getProperty("ssh.user"));
        config.setSshPassword(properties.getProperty("ssh.password"));

        return config;
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
