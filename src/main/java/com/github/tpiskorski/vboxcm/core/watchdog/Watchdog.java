package com.github.tpiskorski.vboxcm.core.watchdog;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.util.Objects;

public class Watchdog {
    private StringProperty server = new SimpleStringProperty();
    private StringProperty vmName = new SimpleStringProperty();
    private StringProperty watchdogServer = new SimpleStringProperty();

    static Callback<Watchdog, Observable[]> extractor() {
        return (Watchdog watchdog) -> new Observable[]{watchdog.serverProperty(), watchdog.vmNameProperty(), watchdog.watchdogServerProperty()};
    }

    public String getServer() {
        return server.get();
    }

    public void setServer(String server) {
        this.server.set(server);
    }

    public StringProperty serverProperty() {
        return server;
    }

    public String getVmName() {
        return vmName.get();
    }

    public void setVmName(String vmName) {
        this.vmName.set(vmName);
    }

    public StringProperty vmNameProperty() {
        return vmName;
    }

    public String getWatchdogServer() {
        return watchdogServer.get();
    }

    public void setWatchdogServer(String watchdogServer) {
        this.watchdogServer.set(watchdogServer);
    }

    public StringProperty watchdogServerProperty() {
        return watchdogServer;
    }

    @Override public int hashCode() {
        return Objects.hash(getServer(), getVmName(), getWatchdogServer());
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof Watchdog)) {
            return false;
        }
        Watchdog that = (Watchdog) obj;

        return Objects.equals(this.getServer(), that.getServer())
            && Objects.equals(this.getVmName(), that.getVmName())
            && Objects.equals(this.getWatchdogServer(), that.getWatchdogServer());
    }
}
