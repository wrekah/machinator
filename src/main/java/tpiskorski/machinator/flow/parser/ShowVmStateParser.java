package tpiskorski.machinator.flow.parser;

import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.model.vm.VirtualMachineState;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class ShowVmStateParser {

    public VirtualMachineState parse(CommandResult commandResult) throws IOException {
        String std = commandResult.getStd();

        Properties properties = new Properties();
        properties.load(new StringReader(std));

        return VirtualMachineState.parse(properties.getProperty("VMState"));
    }
}
