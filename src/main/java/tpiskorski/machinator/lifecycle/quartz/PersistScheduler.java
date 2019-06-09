package tpiskorski.machinator.lifecycle.quartz;

import tpiskorski.machinator.lifecycle.state.manager.PersistenceType;

public interface PersistScheduler {
    void schedulePersistence(PersistenceType persistenceType);
}
