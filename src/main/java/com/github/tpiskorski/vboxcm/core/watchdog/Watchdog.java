package com.github.tpiskorski.vboxcm.core.watchdog;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class Watchdog {

    private VirtualMachine virtualMachine;
    private Server watchdogServer;

    public VirtualMachine getVirtualMachine() {
        return virtualMachine;
    }

    public void setVirtualMachine(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public SimpleStringProperty x() {
        return new SimpleStringProperty(getVirtualMachine().getServerAddress());
    }

    public Server getWatchdogServer() {
        return watchdogServer;
    }

    public void setWatchdogServer(Server watchdogServer) {
        this.watchdogServer = watchdogServer;
    }

    @Override public int hashCode() {
        return Objects.hash(getVirtualMachine(), getWatchdogServer());
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof Watchdog)) {
            return false;
        }
        Watchdog that = (Watchdog) obj;

        return Objects.equals(this.getVirtualMachine(), that.getVirtualMachine())
            && Objects.equals(this.getWatchdogServer(), that.getWatchdogServer());
    }
}
