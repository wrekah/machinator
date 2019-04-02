package tpiskorski.vboxcm.persistance;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ShutdownService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownService.class);

    private final ConfigurableApplicationContext springContext;

    @Autowired public ShutdownService(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    public void shutdown() {
        LOGGER.info("Shutting down application...");
        springContext.close();
        Platform.exit();
        LOGGER.info("Shutdown complete");
    }
}
