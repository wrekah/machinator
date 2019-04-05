package tpiskorski.vboxcm.shutdown.state.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultAppStatePersister implements AppStatePersister {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStatePersister.class);

    private final List<Persister> persisters;

    @Autowired public DefaultAppStatePersister(List<Persister> persisters) {
        this.persisters = persisters;
    }

    public void persist() {
        LOGGER.info("Starting app state persistence");
        persisters.forEach(Persister::persist);
        LOGGER.info("Persisted app state!");
    }
}
