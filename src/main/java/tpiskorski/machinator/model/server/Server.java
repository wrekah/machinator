package tpiskorski.machinator.model.server;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    private static final String LOCALHOST = "local";

    private String address;
    private String port;
    private Credentials credentials;
    private ServerType serverType;

    private Lock lock = new ReentrantLock();
    private ObjectProperty<ServerState> serverState = new SimpleObjectProperty<>();

    public Server(Credentials credentials, String address, String port) {
        this(address, port);
        this.credentials = credentials;
    }

    public Server(String address, String port) {
        setAddress(address);
        setPort(port);
        setServerState(ServerState.UNKNOWN);

        if (address.equals(LOCALHOST)) {
            setServerType(ServerType.LOCAL);
        } else {
            setServerType(ServerType.REMOTE);
        }
    }

    static Callback<Server, Observable[]> extractor() {
        return (Server server) -> new Observable[]{server.serverStateProperty()};
    }

    public void lock() {
        lock.lock();
    }

    public boolean tryLocking() {
        return lock.tryLock();
    }

    public void unlock() {
        lock.unlock();
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSimpleAddress() {
        if (getServerType() == ServerType.LOCAL) {
            return getAddress();
        } else {
            return getAddress() + ":" + getPort();
        }
    }

    public ServerState getServerState() {
        return serverState.get();
    }

    public void setServerState(ServerState serverState) {
        this.serverState.set(serverState);
    }

    public ObjectProperty<ServerState> serverStateProperty() {
        return serverState;
    }

    @Override public int hashCode() {
        return Objects.hash(getAddress(), getPort());
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof Server)) {
            return false;
        }
        Server that = (Server) obj;
        return Objects.equals(this.getAddress(), that.getAddress()) && Objects.equals(this.getPort(), that.getPort());
    }

    @Override public String toString() {
        return getSimpleAddress();
    }
}
