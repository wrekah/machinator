package tpiskorski.machinator.flow.parser;

import org.springframework.stereotype.Component;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.model.vm.VirtualMachineState;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

@Component
public class ShowVmInfoParser {

    public VmInfo parse(CommandResult commandResult) {
        String std = commandResult.getStd();

        Properties properties = loadProperties(std);

        int cpus = parseInt(properties, "cpus");
        int memory = parseInt(properties, "memory");
        VirtualMachineState state = getVmState(properties);

        VmInfo vmInfo = new VmInfo();
        vmInfo.setCpus(cpus);
        vmInfo.setMemory(memory);
        vmInfo.setState(state);

        return vmInfo;
    }

    private VirtualMachineState getVmState(Properties properties) {
        try {
            return VirtualMachineState.parse(properties.getProperty("VMState"));
        } catch (Exception e) {
            return VirtualMachineState.COULD_NOT_PARSE;
        }
    }

    private int parseInt(Properties properties, String cpus) {
        try {
            return Integer.parseInt(properties.getProperty(cpus));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Properties loadProperties(String std) {
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(std));
            return properties;
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }
}
