package tpiskorski.machinator.lifecycle.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.lifecycle.state.manager.PersistenceType;

@Profile("dev")
@Service
public class NoopPersistScheduler implements PersistScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatePersistScheduler.class);

    @Override public void schedulePersistence(PersistenceType persistenceType) {
        LOGGER.debug("NOOP persistence for {}", persistenceType);
    }
}
