package tpiskorski.machinator.flow.executor.poll;

import tpiskorski.machinator.flow.executor.ExecutionException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PollExecutor {

    private static final int MAX_TRIES = 10;
    private static final int TRY_INTERVAL_IN_SECONDS = 1;

    public void pollExecute(PollCommand pollCommand) {
        try {
            int tries = 0;
            boolean result = false;
            while (tries < MAX_TRIES && !result) {
                result = pollCommand.executeAndCheckIfSuccess();
                TimeUnit.SECONDS.sleep(TRY_INTERVAL_IN_SECONDS);
                tries++;
            }
            if (!result) {
                throw new ExecutionException("Poll command failed after max tries");
            }
        } catch (InterruptedException | IOException e) {
            throw new ExecutionException(e);
        }
    }
}
