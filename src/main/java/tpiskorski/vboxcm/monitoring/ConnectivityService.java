package tpiskorski.vboxcm.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import tpiskorski.vboxcm.command.Command;
import tpiskorski.vboxcm.command.CommandResult;
import tpiskorski.vboxcm.command.LocalMachineCommandExecutor;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

@org.springframework.stereotype.Service
public class ConnectivityService extends Service<Void> {

    @Autowired private LocalMachineCommandExecutor localMachineCommandExecutor;

    @Override protected Task<Void> createTask() {
        return new Task<>() {
            @Override protected Void call() throws Exception {
                CommandResult result = localMachineCommandExecutor.execute(Command.of("sh", "-c", "VBoxManage --version"));
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
