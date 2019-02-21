package com.github.tpiskorski.vboxcm.stub;


import com.github.tpiskorski.vboxcm.domain.Server;
import com.github.tpiskorski.vboxcm.domain.ServerRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Profile("stub")
@Component
public class ServerListStubGenerator implements InitializingBean {

    private final ServerRepository serverRepository;

    @Autowired public ServerListStubGenerator(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @Override public void afterPropertiesSet() {
        int serversInList = ThreadLocalRandom.current().nextInt(0, 10);
        IntStream.range(0, serversInList).forEach(num -> serverRepository.add(new Server("localhost:" + 10 * num)));
    }
}
