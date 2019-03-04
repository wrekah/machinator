package com.github.tpiskorski.vboxcm.stub;

import com.github.tpiskorski.vboxcm.server.Server;
import com.github.tpiskorski.vboxcm.server.ServerService;
import com.github.tpiskorski.vboxcm.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.vm.VirtualMachineRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Profile("stub")
@Component
public class ServerListStubGenerator implements InitializingBean {

    private final ServerService serverService;
    private final VirtualMachineRepository virtualMachineRepository;

    @Autowired
    public ServerListStubGenerator(ServerService serverService, VirtualMachineRepository virtualMachineRepository) {
        this.serverService = serverService;
        this.virtualMachineRepository = virtualMachineRepository;
    }

    @Override public void afterPropertiesSet() {
        int serversInList = ThreadLocalRandom.current().nextInt(1, 10);
        IntStream.range(0, serversInList).forEach(num -> {
            String address = "localhost:" + 10 * num;
            serverService.add(new Server(address));
            int virtualMachines = ThreadLocalRandom.current().nextInt(1, 3);
            for (int i = 0; i < virtualMachines; i++) {
                VirtualMachine vm = new VirtualMachine();
                vm.setCpuCores(4);
                vm.setServer(address);
                vm.setState("ON");
                vm.setVmName("vm@" + i);
                vm.setRamMemory(1024);
                virtualMachineRepository.add(vm);
            }
        });
    }
}
