package com.github.tpiskorski.vboxcm.stub.generator;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineState;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VmGenerator {

    public List<VirtualMachine> generateVirtualMachines(Server server, int virtualMachinesNumber) {
        return IntStream.rangeClosed(1, virtualMachinesNumber)
            .mapToObj(i -> {
                VirtualMachine vm = new VirtualMachine();
                vm.setId("generated");
                vm.setCpuCores(cpuCores());
                vm.setServer(server.getAddress());
                vm.setVmName(vmName(i));
                vm.setRamMemory(ram());
                vm.setState(state());
                return vm;
            })
            .collect(Collectors.toList());
    }

    private String vmName(int i) {
        switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0:
                return "centos " + i;
            case 1:
                return "ubuntu " + i;
            case 2:
                return "fedora " + i;
            default:
                return "arch_linux " + i;
        }
    }

    private int cpuCores() {
        switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 4;
            default:
                return 0;
        }
    }

    private VirtualMachineState state() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return VirtualMachineState.ON;
        } else {
            return VirtualMachineState.OFF;
        }
    }

    private int ram() {
        switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0:
                return 1024;
            case 1:
                return 512;
            case 2:
                return 2048;
            default:
                return 0;
        }
    }
}
