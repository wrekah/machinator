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

        int cpus = Integer.parseInt(properties.getProperty("cpus"));
        int memory = Integer.parseInt(properties.getProperty("memory"));
        VirtualMachineState state = VirtualMachineState.parse(properties.getProperty("VMState"));

        VmInfo vmInfo = new VmInfo();
        vmInfo.setCpus(cpus);
        vmInfo.setMemory(memory);
        vmInfo.setState(state);

        return vmInfo;
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
