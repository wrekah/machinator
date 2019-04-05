package tpiskorski.vboxcm.shutdown.state.restore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tpiskorski.vboxcm.shutdown.state.persist.Persister;

import java.util.List;

@Profile("!dev")
@Service
public class AppStateRestorer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStateRestorer.class);

    private final List<Persister> persisters;

    @Autowired public AppStateRestorer(List<Persister> persisters) {
        this.persisters = persisters;
    }

    @Override public void afterPropertiesSet() {
        LOGGER.info("Started restoring app state...");
        persisters.forEach(Persister::restore);
        LOGGER.info("App state restored");
    }
}
