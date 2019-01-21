package com.github.tpiskorski.vboxcm.config.io;

import com.github.tpiskorski.vboxcm.config.Config;
import org.springframework.stereotype.Component;

@Component
public class InternalConfigLoader {
    public Config loadInternalConfig() {
        return new Config();
    }
}
