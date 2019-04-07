package tpiskorski.machinator.config.io.resource;

import tpiskorski.machinator.config.io.exception.ConfigNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class PropertiesReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceReader.class);

    Properties read(Resource resource) {
        try (InputStream in = resource.getInputStream()) {
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            LOGGER.info("Could not read configuration.", e);
            throw new ConfigNotFoundException();
        }
    }
}
