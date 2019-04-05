package tpiskorski.vboxcm.shutdown.state.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.server.ServerService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServerPersister extends Persister  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPersister.class);


    private final ServerService serverService;

    @Autowired
    public ServerPersister(ServerService serverService) {
        this.serverService = serverService;
    }

    @Override public void persist()   {
        LOGGER.info("Starting servers persistence");

        List<SerializableServer> toSerialize = serverService.getServers().stream()
            .map(SerializableServer::new)
            .collect(Collectors.toList());

        try {
            objectPersister.persist("servers.dat", toSerialize);
            LOGGER.info("Persisted servers!");
        } catch (IOException ex) {
            LOGGER.error("Could not persist servers", ex);
        }
    }
}
