package tpiskorski.machinator.flow.javafx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.quartz.service.VboxChecker;
import tpiskorski.machinator.flow.quartz.service.VmLister;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.server.ServerService;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.ui.core.PlatformThreadAction;
import tpiskorski.machinator.ui.core.PlatformThreadUpdater;

import java.util.List;

@Service
public class AddServerService {

    @Autowired private VboxChecker vboxChecker;
    @Autowired private ServerService serverService;

    @Autowired private PlatformThreadUpdater platformThreadUpdater;
    @Autowired private VmLister vmLister;

    public void add(Server server) {
        String vboxVersion = vboxChecker.getVboxVersion(server);
        server.setVboxVersion(vboxVersion);

        List<VirtualMachine> vms = vmLister.list(server);
        platformThreadUpdater.runLater(addServerAndVmsAction(server, vms));
    }

    PlatformThreadAction addServerAndVmsAction(Server server, List<VirtualMachine> vms) {
        return () -> {
            serverService.add(server);
            serverService.refresh(server, vms);
        };
    }
}
