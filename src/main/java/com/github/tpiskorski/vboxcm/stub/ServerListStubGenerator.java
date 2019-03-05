package com.github.tpiskorski.vboxcm.stub;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Profile("stub")
@Component
public class ServerListStubGenerator implements InitializingBean {

    private final ServerService serverService;

    @Autowired
    public ServerListStubGenerator(ServerService serverService) {
        this.serverService = serverService;
    }

    @Override public void afterPropertiesSet() {
        int numberOfServers = ThreadLocalRandom.current().nextInt(1, 10);

        generateServers(numberOfServers);
    }

    void generateServers(int numberOfServers) {
        IntStream.range(0, numberOfServers).forEach(num -> {
            String address = "localhost:" + 10 * num;
            serverService.add(new Server(address));
        });
    }
}
