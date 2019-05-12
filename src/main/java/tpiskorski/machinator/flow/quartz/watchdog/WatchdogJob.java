package tpiskorski.machinator.flow.quartz.watchdog;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class WatchdogJob extends QuartzJobBean {
    @Override protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        
    }
}
