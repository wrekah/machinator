package tpiskorski.vboxcm.config.io.resource;

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Properties;

class FileSystemResourceReader implements ResourceReader {

    private PropertiesReader propertiesReader = new PropertiesReader();
    private FileSystemResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();

    @Override public Properties read(String filePath) {
        Resource resource = fileSystemResourceLoader.getResource(filePath);
        return propertiesReader.read(resource);
    }
}
