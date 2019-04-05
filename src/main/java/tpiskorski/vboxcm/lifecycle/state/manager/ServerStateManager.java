package tpiskorski.vboxcm.lifecycle.state.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.vboxcm.core.server.ServerService;
import tpiskorski.vboxcm.lifecycle.state.serialize.model.SerializableServer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServerStateManager extends StateManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerStateManager.class);

    private final ServerService serverService;

    @Autowired
    public ServerStateManager(ServerService serverService) {
        this.serverService = serverService;
    }

    @Override public String getPersistResourceFileName() {
        return "./data/servers.dat";
    }

    @Override public void persist() {
        LOGGER.info("Starting servers persistence");

        List<SerializableServer> toSerialize = serverService.getServers().stream()
            .map(SerializableServer::new)
            .collect(Collectors.toList());

        try {
            objectPersister.persist(getPersistResourceFileName(), toSerialize);
            LOGGER.info("Persisted servers!");
        } catch (IOException ex) {
            LOGGER.error("Could not persist servers", ex);
        }
    }

    @Override public void restore() {
        LOGGER.info("Starting restoring servers state");

        try {
            objectRestorer.restore(SerializableServer.class, getPersistResourceFileName()).stream()
                .map(SerializableServer::toServer)
                .collect(Collectors.toList())
                .forEach(serverService::add);

            LOGGER.info("Servers state restored");
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.error("Could not restore servers state", ex);
        }
    }
}
