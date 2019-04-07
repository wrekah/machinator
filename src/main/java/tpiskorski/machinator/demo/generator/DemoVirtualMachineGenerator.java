package tpiskorski.machinator.demo.generator;

import tpiskorski.machinator.core.server.Server;
import tpiskorski.machinator.core.server.ServerService;
import tpiskorski.machinator.core.vm.VirtualMachine;
import tpiskorski.machinator.core.vm.VirtualMachineService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Profile("demo")
@DependsOn("demoServerGenerator")
@Component
public class DemoVirtualMachineGenerator implements InitializingBean {

    private final ServerService serverService;
    private final VirtualMachineService virtualMachineService;

    private VmGenerator vmGenerator = new VmGenerator();

    @Autowired
    public DemoVirtualMachineGenerator(ServerService serverService, VirtualMachineService virtualMachineService) {
        this.serverService = serverService;
        this.virtualMachineService = virtualMachineService;
    }

    @Override public void afterPropertiesSet() {
        for (Server server : serverService.getServers()) {
            int virtualMachinesNumber = ThreadLocalRandom.current().nextInt(1, 3);

            List<VirtualMachine> vms = vmGenerator.generateVirtualMachines(server, virtualMachinesNumber);
            virtualMachineService.add(vms);
        }
    }
}
