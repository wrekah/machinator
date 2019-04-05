package tpiskorski.vboxcm.lifecycle.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tpiskorski.vboxcm.lifecycle.state.manager.StateManager;

import java.util.List;

@Profile("!dev")
@Service
public class DefaultAppStatePersister implements AppStatePersister {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStatePersister.class);

    private final List<StateManager> stateManagers;

    @Autowired public DefaultAppStatePersister(List<StateManager> stateManagers) {
        this.stateManagers = stateManagers;
    }

    public void persist() {
        LOGGER.info("Starting app state persistence");
        stateManagers.forEach(StateManager::persist);
        LOGGER.info("Persisted app state!");
    }
}
