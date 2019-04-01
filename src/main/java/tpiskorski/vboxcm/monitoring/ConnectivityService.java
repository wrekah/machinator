package tpiskorski.vboxcm.monitoring;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;
import tpiskorski.vboxcm.command.*;

@org.springframework.stereotype.Service
public class ConnectivityService extends Service<Void> {

    @Autowired private LocalMachineCommandExecutor localMachineCommandExecutor;
    @Autowired private CommandFactory commandFactory;

    @Override protected Task<Void> createTask() {
        return new Task<>() {
            @Override protected Void call() throws Exception {
                Command command = commandFactory.make(BaseCommand.IS_VBOX_INSTALLED);
                CommandResult result = localMachineCommandExecutor.execute(command);
                if (result.isFailed()) {
                    throw new RuntimeException("Failing the task");
                } else {
                    this.succeeded();
                }
                return null;
            }
        };
    }
}
