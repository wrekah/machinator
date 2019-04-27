package tpiskorski.machinator.lifecycle.state.serialize.model;

import tpiskorski.machinator.model.server.Credentials;
import tpiskorski.machinator.model.server.Server;

import java.io.Serializable;

public class SerializableServer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String address;
    private String port;
    private Credentials credentials;

    public SerializableServer(Server server) {
        this.address = server.getAddress();
        this.port = server.getPort();
        this.credentials = server.getCredentials();
    }

    public Server toServer() {
        return new Server(credentials, address, port);
    }
}
