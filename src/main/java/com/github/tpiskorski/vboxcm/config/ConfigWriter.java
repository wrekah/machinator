package com.github.tpiskorski.vboxcm.config;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

@Component
public class ConfigWriter {

    private Writer writer = new Writer();
    private PropertiesConfigConverter converter = new PropertiesConfigConverter();

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public void setConverter(PropertiesConfigConverter converter) {
        this.converter = converter;
    }

    public void write(String filePath, Config config) {
        Properties properties = converter.convert(config);
        System.out.println(writer);

        writer.write(filePath, properties);
    }

      class Writer {
        void write(String filePath, Properties prop) {
            try (OutputStream outFile = Files.newOutputStream(Paths.get(filePath), StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
                prop.store(outFile, null);
            } catch (IOException e) {
                throw new ConfigNotFoundException();
            }

        }
    }

}

 