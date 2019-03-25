package tpiskorski.vboxcm.config.io;

import tpiskorski.vboxcm.config.Config;
import tpiskorski.vboxcm.config.io.resource.ResourceReader;
import tpiskorski.vboxcm.config.io.resource.ResourceReaderFactory;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class ConfigReader {

    private ResourceReaderFactory resourceReaderFactory = new ResourceReaderFactory();
    private PropertiesConfigConverter converter = new PropertiesConfigConverter();

    public Config read(String filePath) {
        Properties properties = readProperties(filePath);
        return converter.convert(properties);
    }

    private Properties readProperties(String filePath) {
        ResourceReader resourceReader = resourceReaderFactory.get(filePath);
        return resourceReader.read(filePath);
    }
}
