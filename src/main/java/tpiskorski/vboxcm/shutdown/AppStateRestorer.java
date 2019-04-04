package tpiskorski.vboxcm.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.server.ServerService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.stream.Collectors;

@Profile("!dev")
@Service
public class AppStateRestorer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStateRestorer.class);

    private final ServerService serverService;

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
        List<SerializableServer> serializableServers = readSerializedServers();

        return serializableServers.stream()
            .map(SerializableServer::toServer)
            .collect(Collectors.toList());
    }

    private List<SerializableServer> readSerializedServers() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("servers.dat");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        List<SerializableServer> serializableServers = (List<SerializableServer>) objectInputStream.readObject();
        objectInputStream.close();
        return serializableServers;
    }
}
