package tpiskorski.vboxcm.shutdown.state.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.vboxcm.core.backup.BackupService;
import tpiskorski.vboxcm.core.server.ServerService;
import tpiskorski.vboxcm.core.watchdog.WatchdogService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultAppStatePersister implements AppStatePersister {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStatePersister.class);

    private final ServerService serverService;

    private ObjectPersister objectPersister = new ObjectPersister();

    @Autowired
    public DefaultAppStatePersister(ServerService serverService) {
        this.serverService = serverService;
    }

    public void persist() {
        try {
            LOGGER.info("Starting server persistance");
            serializeServers();
            LOGGER.info("Persisted servers");
        } catch (Exception ex) {
            LOGGER.error("Could not persist servers", ex);
        }
    }

    private void serializeServers() throws IOException {
        List<SerializableServer> toSerialize = serverService.getServers().stream()
            .map(SerializableServer::new)
            .collect(Collectors.toList());

        objectPersister.persist("servers.dat", toSerialize);
    }
}
