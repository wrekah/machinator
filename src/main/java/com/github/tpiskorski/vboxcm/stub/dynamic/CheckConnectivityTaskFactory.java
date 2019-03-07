package com.github.tpiskorski.vboxcm.stub.dynamic;

import com.github.tpiskorski.vboxcm.core.server.Server;
import org.springframework.stereotype.Component;

@Component
public class CheckConnectivityTaskFactory {
    public CheckConnectivityTask taskFor(Server server) {
        return new CheckConnectivityTask(server);
    }
}
