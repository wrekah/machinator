package com.github.tpiskorski.vboxcm.stub.generator;

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
        int numberOfServers = ThreadLocalRandom.current().nextInt(2, 10);

        generateServers(numberOfServers);
    }

    void generateServers(int numberOfServers) {
        IntStream.range(0, numberOfServers).forEach(num -> {
            String address = generateRandomAddress();
            serverService.add(new Server(address));
        });
    }

    private String generateRandomAddress() {
        String randomIp = generateRandomIp();
        int randomPort = generateRandomPort();

        return randomIp + ":" + randomPort;
    }

    private String generateRandomIp() {
        return segment() + "." + segment() + "." + segment() + "." + segment();
    }

    private int segment() {
        return ThreadLocalRandom.current().nextInt(256);
    }

    private int generateRandomPort() {
        return ThreadLocalRandom.current().nextInt(1, 1024);
    }
}
