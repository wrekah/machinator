package tpiskorski.machinator.flow.executor.poll;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PollExecutor {

    private static int POLL_TIMES = 10;
    private static int POLL_INTERVAL_IN_SECONDS = 1;

    public void pollExecute(PollCommand pollCommand) throws InterruptedException, IOException {
        int cycles = 0;
        while (cycles < POLL_TIMES && !pollCommand.executeAndCheckIfSuccess()) {
            TimeUnit.SECONDS.sleep(POLL_INTERVAL_IN_SECONDS);
            cycles++;
        }
    }
}
