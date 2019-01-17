package com.github.tpiskorski.vboxcm.config;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class ConfigWriter {

    private Writer writer = new Writer();
    private PropertiesConfigConverter converter = new PropertiesConfigConverter();

    public void write(Config config) {
        Properties properties = converter.convert(config);
        writer.write(properties);
    }

    class Writer {
        void write(Properties prop) {
            try (OutputStream outFile = Files.newOutputStream(Paths.get(""), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
                prop.store(outFile, null);
            } catch (IOException e) {
                throw new ConfigNotFoundException();
            }

        }
    }

}

 