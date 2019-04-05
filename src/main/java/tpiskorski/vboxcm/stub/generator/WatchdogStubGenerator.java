package tpiskorski.vboxcm.stub.generator;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tpiskorski.vboxcm.core.server.Server;
import tpiskorski.vboxcm.core.server.ServerService;
import tpiskorski.vboxcm.core.vm.VirtualMachine;
import tpiskorski.vboxcm.core.vm.VirtualMachineService;
import tpiskorski.vboxcm.core.watchdog.Watchdog;
import tpiskorski.vboxcm.core.watchdog.WatchdogService;

import java.util.Optional;
import java.util.stream.IntStream;

@Profile("stub")
@DependsOn("virtualMachineStubGenerator")
@Component
public class WatchdogStubGenerator implements InitializingBean {

    private final WatchdogService watchdogService;

    private final ServerService serverService;
    private final VirtualMachineService virtualMachineService;

    @Autowired
    public WatchdogStubGenerator(WatchdogService watchdogService, ServerService serverService, VirtualMachineService virtualMachineService) {
        this.watchdogService = watchdogService;
        this.serverService = serverService;
        this.virtualMachineService = virtualMachineService;
    }

    @Override public void afterPropertiesSet() {
        ObservableList<Server> servers = serverService.getServers();
        ObservableList<VirtualMachine> vms = virtualMachineService.getVms();

        int size = vms.size();
        IntStream.range(0, size)
            .limit(3)
            .mapToObj(vms::get)
            .map(vm -> createBackupForVm(servers, vm))
            .forEach(watchdogService::add);
    }

    private Watchdog createBackupForVm(ObservableList<Server> servers, VirtualMachine virtualMachine) {
        Optional<Server> otherServer = servers.stream()
            .filter(server -> !server.getSimpleAddress().equals(virtualMachine.getServer().getSimpleAddress()))
            .findAny();

        return new Watchdog(virtualMachine, otherServer.get());
    }
}