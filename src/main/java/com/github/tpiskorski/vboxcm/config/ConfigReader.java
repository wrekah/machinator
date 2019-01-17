package com.github.tpiskorski.vboxcm.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class ConfigReader {

    private Reader reader = new Reader();
    private PropertiesConfigConverter converter = new PropertiesConfigConverter();

    public Config read() {
        Properties properties = reader.read();
        return converter.convert(properties);
    }

    public class Reader {
        public Properties read() {
            try (InputStream in = Files.newInputStream(Paths.get(""), StandardOpenOption.READ)) {
                Properties properties = new Properties();
                properties.load(in);
                return properties;
            } catch (IOException e) {
                throw new ConfigNotFoundException();
            }
        }

    }
}
