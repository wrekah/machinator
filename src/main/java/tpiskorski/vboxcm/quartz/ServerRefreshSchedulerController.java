package tpiskorski.vboxcm.quartz;

import org.quartz.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import tpiskorski.vboxcm.core.server.Server;

import java.util.UUID;

@Controller
public class ServerRefreshSchedulerController implements InitializingBean {

    private final Scheduler scheduler;

    @Autowired public ServerRefreshSchedulerController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    public void scheduleRegularScans() throws SchedulerException {

        JobDetail jobDetail = JobBuilder.newJob(ServerScanJob.class)
            .withIdentity(UUID.randomUUID().toString(), "serverScan")
            .storeDurably()
            .build();


        CronTrigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(jobDetail.getKey().getName(), "serverScan-trigger")
            .withSchedule(CronScheduleBuilder.cronSchedule( "0/10 * * ? * *"))
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    @Override public void afterPropertiesSet() throws Exception {
        scheduleRegularScans();
    }
}
