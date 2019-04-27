package tpiskorski.machinator.flow.parser;

import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.model.vm.VirtualMachineState;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class ShowVmInfoParser {

    public ShowVmInfoUpdate parse(CommandResult commandResult) throws IOException {
        String std = commandResult.getStd();

        Properties properties = new Properties();
        properties.load(new StringReader(std));

        int cpus = Integer.parseInt(properties.getProperty("cpus"));
        int memory = Integer.parseInt(properties.getProperty("memory"));
        VirtualMachineState state = VirtualMachineState.parse(properties.getProperty("VMState"));

        ShowVmInfoUpdate showVmInfoUpdate = new ShowVmInfoUpdate();
        showVmInfoUpdate.setCpus(cpus);
        showVmInfoUpdate.setMemory(memory);
        showVmInfoUpdate.setState(state);

        return showVmInfoUpdate;
    }
}
