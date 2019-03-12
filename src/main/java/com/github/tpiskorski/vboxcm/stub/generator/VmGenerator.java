package com.github.tpiskorski.vboxcm.stub.generator;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineState;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VmGenerator {

    public List<VirtualMachine> generateVirtualMachines(Server server, int virtualMachinesNumber) {
        return IntStream.rangeClosed(1, virtualMachinesNumber)
            .mapToObj(i -> {
                VirtualMachine vm = new VirtualMachine();
                vm.setCpuCores(4);
                vm.setServer(server.getAddress());
                vm.setVmName("vm@" + i);
                vm.setRamMemory(1024);
                vm.setState(VirtualMachineState.ON);
                return vm;
            })
            .collect(Collectors.toList());
    }
}
