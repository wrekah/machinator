package tpiskorski.machinator.lifecycle.state.serialize.model;

import tpiskorski.machinator.model.server.Credentials;

import java.io.Serializable;

public class SerializableCredentials implements Serializable {

    private static final long serialVersionUID = 1L;

    private String user;
    private String password;

    SerializableCredentials(Credentials credentials) {
        this.user = credentials.getUser();
        this.password = credentials.getPassword();
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    Credentials toCredentials() {
        return new Credentials(user, password);
    }
}
