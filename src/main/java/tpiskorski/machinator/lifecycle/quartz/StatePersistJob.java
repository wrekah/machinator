package tpiskorski.machinator.lifecycle.quartz;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.lifecycle.state.manager.PersistenceType;
import tpiskorski.machinator.lifecycle.state.manager.StateManager;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StatePersistJob extends QuartzJobBean {

    static final String PERSISTENCE_TYPE_KEY = "persistenceType";

    private static final Logger LOGGER = LoggerFactory.getLogger(StatePersistJob.class);

    private final Map<PersistenceType, StateManager> stateManagers;

    @Autowired
    public StatePersistJob(List<StateManager> stateManagers) {
        this.stateManagers = stateManagers.stream()
            .collect(Collectors.toMap(StateManager::getPersistenceType, Function.identity()));
    }

    @Override protected void executeInternal(JobExecutionContext context) {
        PersistenceType persistenceType = retrievePersistenceType(context.getMergedJobDataMap());
        LOGGER.debug("Started persistence job for {}", persistenceType);
        stateManagers.get(persistenceType).persist();
        LOGGER.debug("Finished persistence job for {}", persistenceType);
    }

    private PersistenceType retrievePersistenceType(JobDataMap context) {
        return ((PersistenceType) context.get(PERSISTENCE_TYPE_KEY));
    }
}
