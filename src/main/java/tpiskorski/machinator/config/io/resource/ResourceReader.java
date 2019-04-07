package tpiskorski.machinator.config.io.resource;

import java.util.Properties;

public interface ResourceReader {
    Properties read(String filePath);
}
