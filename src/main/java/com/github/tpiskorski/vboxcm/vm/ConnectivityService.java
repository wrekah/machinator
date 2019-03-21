package com.github.tpiskorski.vboxcm.vm;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

@org.springframework.stereotype.Service
public class ConnectivityService extends Service<Void> {

    private LocalhostConnectivityChecker localhostConnectivityChecker = new LocalhostConnectivityChecker();

    @Override protected Task<Void> createTask() {
        return new Task<>() {
            @Override protected Void call() throws Exception {
                CommandResult result = localhostConnectivityChecker.check();
                if (result.isFailed()) {
                    this.failed();
                    ConnectivityService.this.failed();
                    throw new RuntimeException("Failing the task");
                } else {
                    this.succeeded();
                }
                return null;
            }
        };
    }
}
