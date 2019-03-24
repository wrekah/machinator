package com.github.tpiskorski.vboxcm.vm;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineState;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SimpleVmParser {

    List<VirtualMachine> parse(Server server, CommandResult commandResult) {
        String std = commandResult.getStd();
        return Pattern.compile("\n").splitAsStream(std)
            .map(this::mapToVm)
            .peek(virtualMachine -> virtualMachine.setServer(server))
            .collect(Collectors.toList());
    }

    private VirtualMachine mapToVm(String vmString) {
        String vmName = vmString.substring(vmString.indexOf("\"") + 1, vmString.lastIndexOf("\""));
        String vmId = vmString.substring(vmString.indexOf("{") + 1, vmString.lastIndexOf("}"));

        VirtualMachine vm = new VirtualMachine();
        vm.setId(vmId);
        vm.setCpuCores(1);
        vm.setVmName(vmName);
        vm.setRamMemory(1024);
        vm.setState(VirtualMachineState.ON);

        return vm;
    }
}