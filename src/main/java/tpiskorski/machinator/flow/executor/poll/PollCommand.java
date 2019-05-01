package tpiskorski.machinator.flow.executor.poll;

import java.io.IOException;

@FunctionalInterface
public interface PollCommand {
    boolean executeAndCheckIfSuccess() throws IOException, InterruptedException;
}
