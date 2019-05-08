package tpiskorski.machinator.flow.executor.poll;

import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PollExecutor {

    private static final int MAX_TRIES = 10;
    private static final int TRY_INTERVAL_IN_SECONDS = 1;

    public void pollExecute(PollCommand pollCommand) throws JobExecutionException {
        try {
            int tries = 0;
            boolean result = false;
            while (tries < MAX_TRIES && !result) {
                result = pollCommand.executeAndCheckIfSuccess();
                TimeUnit.SECONDS.sleep(TRY_INTERVAL_IN_SECONDS);
                tries++;
            }
            if (!result) {
                throw new JobExecutionException("Poll command failed after max tries");
            }
        } catch (InterruptedException | IOException e) {
            throw new JobExecutionException(e);
        }
    }
}
