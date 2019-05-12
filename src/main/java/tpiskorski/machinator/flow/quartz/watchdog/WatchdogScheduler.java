package tpiskorski.machinator.flow.quartz.watchdog;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.model.watchdog.Watchdog;

@Service
public class WatchdogScheduler  implements InitializingBean {
    public void schedule(Watchdog watchdog) {

    }

    @Override public void afterPropertiesSet() throws Exception {

    }
}
