package tpiskorski.machinator.lifecycle.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.lifecycle.state.manager.StateManager;

import java.util.List;

@Profile("!dev")
@Service
public class AppStateRestorer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStateRestorer.class);

    private final List<StateManager> stateManagers;

    @Autowired public AppStateRestorer(List<StateManager> stateManagers) {
        this.stateManagers = stateManagers;
    }

    @Override public void afterPropertiesSet() {
        LOGGER.info("Started restoring app state...");
        stateManagers.forEach(StateManager::restore);
        LOGGER.info("App state restored");
    }
}
