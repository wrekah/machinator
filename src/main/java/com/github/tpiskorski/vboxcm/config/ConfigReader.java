package com.github.tpiskorski.vboxcm.config;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

@Component
public class ConfigReader {

    private Reader reader = new Reader();
    private PropertiesConfigConverter converter = new PropertiesConfigConverter();

    public Config read(String filePath) {
        Properties properties = reader.read(filePath);
        return converter.convert(properties);
    }

    class Reader {
        public Properties read(String filePath) {
            try (InputStream in = Files.newInputStream(Paths.get(filePath), StandardOpenOption.READ)) {
                Properties properties = new Properties();
                properties.load(in);
                return properties;
            } catch (IOException e) {
                throw new ConfigNotFoundException();
            }
        }
    }
}
