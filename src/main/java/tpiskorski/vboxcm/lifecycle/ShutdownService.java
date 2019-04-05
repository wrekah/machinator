package tpiskorski.vboxcm.lifecycle;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import tpiskorski.vboxcm.lifecycle.state.AppStatePersister;

@Service
public class ShutdownService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownService.class);

    private final ConfigurableApplicationContext springContext;
    private final AppStatePersister appStatePersister;

    @Autowired
    public ShutdownService(ConfigurableApplicationContext springContext, AppStatePersister appStatePersister) {
        this.springContext = springContext;
        this.appStatePersister = appStatePersister;
    }

    public void shutdown() {
        LOGGER.info("Shutting down application...");
        appStatePersister.persist();
        springContext.close();
        Platform.exit();
        LOGGER.info("Shutdown complete");
    }
}
