package tpiskorski.machinator.config.io.resource;

import org.springframework.core.io.ClassPathResource;

public class ResourceReaderFactory {

    public ResourceReader get(String filePath) {
        if (new ClassPathResource(filePath).exists()) {
            return new ClassPathResourceReader();
        } else {
            return new FileSystemResourceReader();
        }
    }
}
