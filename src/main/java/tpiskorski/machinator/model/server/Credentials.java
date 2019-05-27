package tpiskorski.machinator.model.server;

import java.util.Objects;

public class Credentials {

    private String user;
    private String password;

    public Credentials(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Credentials that = (Credentials) obj;

        return Objects.equals(this.user, that.user)
            && Objects.equals(this.password, that.password);
    }

    @Override public int hashCode() {
        return Objects.hash(user, password);
    }
}
