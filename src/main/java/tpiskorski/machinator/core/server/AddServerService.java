package tpiskorski.machinator.core.server;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

@org.springframework.stereotype.Service
public class AddServerService extends Service<Void> {

    private final AddLocalServerService addLocalServerService;
    private final AddRemoteServerService addRemoteServerService;
    private Server server;

    public AddServerService(AddLocalServerService addLocalServerService, AddRemoteServerService addRemoteServerService) {
        this.addLocalServerService = addLocalServerService;
        this.addRemoteServerService = addRemoteServerService;
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
                if (server.getServerType() == ServerType.LOCAL) {
                    addLocalServerService.add(server);
                } else {
                    addRemoteServerService.add(server);
                }
                return null;
            }
        };
    }
}
