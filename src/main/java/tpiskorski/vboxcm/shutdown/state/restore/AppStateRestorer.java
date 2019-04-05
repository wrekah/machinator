package tpiskorski.vboxcm.shutdown.state.restore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.server.ServerService;
import tpiskorski.vboxcm.shutdown.state.persist.SerializableServer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Profile("!dev")
@Service
public class AppStateRestorer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStateRestorer.class);

    private final ServerService serverService;
    private ObjectRestorer objectRestorer = new ObjectRestorer();

    @Autowired public AppStateRestorer(ServerService serverService) {
        this.serverService = serverService;
    }

    @Override public void afterPropertiesSet() throws Exception {
        LOGGER.info("Started restoring app state...");
        List<Server> servers = deserializeServers();

        servers.forEach(serverService::add);
        LOGGER.info("App state restored");
    }

    private List<Server> deserializeServers() throws IOException, ClassNotFoundException {
        List<SerializableServer> serializableServers = objectRestorer.restore(SerializableServer.class, "servers.dat");

        return serializableServers.stream()
            .map(SerializableServer::toServer)
            .collect(Collectors.toList());
    }
}
