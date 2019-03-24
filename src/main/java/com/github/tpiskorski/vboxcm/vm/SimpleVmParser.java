package com.github.tpiskorski.vboxcm.vm;

import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineState;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SimpleVmParser {

    private static final String NEW_LINE = "\n";

    List<VirtualMachine> parse(CommandResult commandResult) {
        String std = commandResult.getStd();
        return Pattern.compile(NEW_LINE).splitAsStream(std)
            .map(this::mapToVm)
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