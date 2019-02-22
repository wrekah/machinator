package com.github.tpiskorski.vboxcm.stub;


import com.github.tpiskorski.vboxcm.domain.Server;
import com.github.tpiskorski.vboxcm.domain.ServerRepository;
import com.github.tpiskorski.vboxcm.domain.VirtualMachine;
import com.github.tpiskorski.vboxcm.domain.VirtualMachineRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Profile("stub")
@Component
public class ServerListStubGenerator implements InitializingBean {

    private final ServerRepository serverRepository;
    private final VirtualMachineRepository virtualMachineRepository;

    @Autowired public ServerListStubGenerator(ServerRepository serverRepository, VirtualMachineRepository virtualMachineRepository) {
        this.serverRepository = serverRepository;
        this.virtualMachineRepository = virtualMachineRepository;
    }

    @Override public void afterPropertiesSet() {
        int serversInList = ThreadLocalRandom.current().nextInt(0, 10);
        IntStream.range(0, serversInList).forEach(num -> {
            String address = "localhost:" + 10 * num;
            serverRepository.add(new Server(address));
            int virtualMachines = ThreadLocalRandom.current().nextInt(1, 3);
            for (int i = 0; i < virtualMachines; i++) {
                VirtualMachine vm = new VirtualMachine();
                vm.setCpuCores(4);
                vm.setServer(address);
                vm.setState("ON");
                vm.setVmName("vm@"+i);
                vm.setRamMemory(1024);
                virtualMachineRepository.add(vm);
            }


        });
    }
}
