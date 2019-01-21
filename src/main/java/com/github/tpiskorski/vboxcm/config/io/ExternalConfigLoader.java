package com.github.tpiskorski.vboxcm.config.io;

import com.github.tpiskorski.vboxcm.config.Config;
import org.springframework.stereotype.Component;

@Component
public class ExternalConfigLoader {
    public Config loadExternalConfig() {
        return new Config();
    }
}
