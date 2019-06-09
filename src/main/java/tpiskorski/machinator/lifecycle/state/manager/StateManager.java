package tpiskorski.machinator.lifecycle.state.manager;

import tpiskorski.machinator.lifecycle.state.serialize.io.ObjectPersister;
import tpiskorski.machinator.lifecycle.state.serialize.io.ObjectRestorer;

public abstract class StateManager {

    protected ObjectPersister objectPersister = new ObjectPersister();
    protected ObjectRestorer objectRestorer = new ObjectRestorer();

    public abstract String getPersistResourceFileName();
    public abstract PersistenceType getPersistenceType();

    public abstract void persist();
    public abstract void restore();
}
