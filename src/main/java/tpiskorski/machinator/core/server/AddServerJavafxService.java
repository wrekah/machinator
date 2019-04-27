package tpiskorski.machinator.core.server;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

@org.springframework.stereotype.Service
public class AddServerJavafxService extends Service<Void> {

    private final AddServerService addServerService;

    private Server server;

    public AddServerJavafxService(AddServerService addServerService) {
        this.addServerService = addServerService;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void start(Server server) {
        this.server = server;
        super.start();
    }

    @Override protected Task<Void> createTask() {
        return new Task<>() {
            @Override protected Void call() throws Exception {
                addServerService.add(server);
                return null;
            }
        };
    }
}
