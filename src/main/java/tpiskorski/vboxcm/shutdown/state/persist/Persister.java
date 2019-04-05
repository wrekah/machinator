package tpiskorski.vboxcm.shutdown.state.persist;

import tpiskorski.vboxcm.shutdown.state.restore.ObjectRestorer;

public abstract class Persister {

    protected ObjectPersister objectPersister = new ObjectPersister();
    protected ObjectRestorer objectRestorer = new ObjectRestorer();

    public abstract String getPersistResourceFileName();

    public abstract void persist();
    public abstract void restore();
}
