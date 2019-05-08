package tpiskorski.machinator.flow.executor;

import tpiskorski.machinator.flow.command.Command;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerType;

import java.io.File;

public class ExecutionContext {

    private String address;
    private int port;
    private String user;
    private String password;
    private boolean isLocal;
    private Command command;
    private File workingDirectory;

    private ExecutionContext(Builder builder) {
        this.address = builder.address;
        this.port = builder.port;
        this.user = builder.user;
        this.password = builder.password;
        this.isLocal = builder.isLocal;
        this.command = builder.command;
        this.workingDirectory = builder.workingDirectory;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    @Override public String toString() {
        return "ExecutionContext{" +
            "address=" + (isLocal ? "local" : address) +
            ", command=" + command +
            ", workingDirectory=" + workingDirectory +
            '}';
    }

    public static class Builder {

        private String address;
        private int port;
        private String user;
        private String password;
        private boolean isLocal;

        private Command command;
        private File workingDirectory;

        public Builder executeOn(Server server) {
            if (server.getServerType() == ServerType.LOCAL) {
                this.isLocal = true;
            } else {
                this.address = server.getAddress();
                this.port = Integer.parseInt(server.getPort());
                this.user = server.getCredentials().getUser();
                this.password = server.getCredentials().getPassword();
                this.isLocal = false;
            }

            return this;
        }

        public Builder workingDirectory(File workingDirectory) {
            this.workingDirectory = workingDirectory;

            return this;
        }

        public Builder command(Command command) {
            this.command = command;

            return this;
        }

        public ExecutionContext build() {
            return new ExecutionContext(this);
        }
    }
}
