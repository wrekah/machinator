package tpiskorski.vboxcm.shutdown.state.persist;

public abstract class Persister {

    protected ObjectPersister objectPersister = new ObjectPersister();

    public abstract void persist();
}
