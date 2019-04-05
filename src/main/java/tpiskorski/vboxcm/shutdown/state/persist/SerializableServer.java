package tpiskorski.vboxcm.shutdown.state.persist;

import tpiskorski.vboxcm.core.server.Server;

import java.io.Serializable;

public class SerializableServer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String address;
    private String port;

    public SerializableServer(Server server) {
        this.address = server.getAddress();
        this.port = server.getPort();
    }

    public Server toServer() {
        return new Server(address, port);
    }
}
