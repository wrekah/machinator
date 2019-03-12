package com.github.tpiskorski.vboxcm.stub.generator;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Profile("stub")
@DependsOn("serverListStubGenerator")
@Component
public class VirtualMachineStubGenerator implements InitializingBean {

    private final ServerService serverService;
    private final VirtualMachineService virtualMachineService;

    private VmGenerator vmGenerator = new VmGenerator();

    @Autowired
    public VirtualMachineStubGenerator(ServerService serverService, VirtualMachineService virtualMachineService) {
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
