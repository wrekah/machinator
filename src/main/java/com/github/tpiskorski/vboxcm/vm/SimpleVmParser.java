package com.github.tpiskorski.vboxcm.vm;

import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineState;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SimpleVmParser {

    List<VirtualMachine> parse(CommandResult commandResult) {
        String std = commandResult.getStd();
        List<VirtualMachine> collect = Pattern.compile("\n").splitAsStream(std)
            .map(this::mapToVm)
            .collect(Collectors.toList());
        return collect;
    }

    private VirtualMachine mapToVm(String vmString) {
        String vmName = vmString.substring(vmString.indexOf("\"") + 1, vmString.lastIndexOf("\""));
        String vmId = vmString.substring(vmString.indexOf("{") + 1, vmString.lastIndexOf("}"));
        System.out.println(vmName);
        System.out.println(vmId);

        VirtualMachine vm = new VirtualMachine();
        vm.setId(vmId);
        vm.setCpuCores(1);
        vm.setServer("localhost");
        vm.setVmName(vmName);
        vm.setRamMemory(1024);
        vm.setState(VirtualMachineState.ON);

        return vm;
    }
}