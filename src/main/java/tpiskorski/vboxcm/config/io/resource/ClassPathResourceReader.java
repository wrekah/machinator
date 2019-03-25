package tpiskorski.vboxcm.config.io.resource;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Properties;

class ClassPathResourceReader implements ResourceReader {

    private PropertiesReader propertiesReader = new PropertiesReader();
    private DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();

    @Override public Properties read(String filePath) {
        Resource resource = defaultResourceLoader.getResource(filePath);
        return propertiesReader.read(resource);
    }
}
