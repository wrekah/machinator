package com.github.tpiskorski.vboxcm.config.io;

import com.github.tpiskorski.vboxcm.config.Config;
import com.github.tpiskorski.vboxcm.config.io.exception.ConfigNotCreatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

@Component
public class ConfigWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigWriter.class);

    private Writer writer = new Writer();
    private PropertiesConfigConverter converter = new PropertiesConfigConverter();

    public void write(String filePath, Config config) {
        Properties properties = converter.convert(config);
        tryToWrite(filePath, properties);
    }

    private void tryToWrite(String filePath, Properties properties) {
        try {
            writer.write(filePath, properties);
        } catch (IOException e) {
            LOGGER.warn("Exception during writing config to file", e);
            throw new ConfigNotCreatedException();
        }
    }

    class Writer {
        void write(String filePath, Properties properties) throws IOException {
            Files.createDirectories(Paths.get(filePath).getParent());
            try (OutputStream outFile = Files.newOutputStream(Paths.get(filePath), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                properties.store(outFile, null);
            }
        }
    }
}

 