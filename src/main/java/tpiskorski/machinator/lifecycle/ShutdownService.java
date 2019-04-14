package tpiskorski.machinator.lifecycle;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.lifecycle.state.AppStatePersister;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ShutdownService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownService.class);

    private final ConfigurableApplicationContext springContext;
    private final AppStatePersister appStatePersister;

    private Lock shutdownLock = new ReentrantLock();
    private boolean isShutdownComplete = false;

    @Autowired
    public ShutdownService(ConfigurableApplicationContext springContext, AppStatePersister appStatePersister) {
        this.springContext = springContext;
        this.appStatePersister = appStatePersister;
    }

    public void shutdown() {
        try {
            shutdownLock.lock();

            if (isShutdownComplete) {
                return;
            }

            LOGGER.info("Shutting down application...");

            appStatePersister.persist();
            springContext.close();
            Platform.exit();
            isShutdownComplete = true;

            LOGGER.info("Shutdown complete");
        } finally {
            shutdownLock.unlock();
        }
    }
}
