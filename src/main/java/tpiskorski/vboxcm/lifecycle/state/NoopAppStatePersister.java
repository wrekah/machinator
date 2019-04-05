package tpiskorski.vboxcm.lifecycle.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;

@Profile("dev")
public class NoopAppStatePersister implements AppStatePersister {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStatePersister.class);

    @Override public void persist() {
        LOGGER.info("Not persisting anything because spring dev profile is active");
    }
}
