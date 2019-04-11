package tpiskorski.machinator.lifecycle.state.serialize.model;

import tpiskorski.machinator.core.watchdog.Watchdog;

import java.io.Serializable;

public class SerializableWatchdog implements Serializable {

    private static final long serialVersionUID = 1L;

    private SerializableVirtualMachine serializableVirtualMachine;
    private SerializableServer serializableWatchdogServer;

    public SerializableWatchdog(Watchdog watchdog) {
        this.serializableVirtualMachine = new SerializableVirtualMachine(watchdog.getVirtualMachine());
        this.serializableWatchdogServer = new SerializableServer(watchdog.getWatchdogServer());
    }

    public Watchdog toWatchdog() {
        return new Watchdog(serializableVirtualMachine.toVirtualMachine(), serializableWatchdogServer.toServer());
    }
}