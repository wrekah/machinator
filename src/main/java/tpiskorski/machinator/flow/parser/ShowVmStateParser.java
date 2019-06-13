package tpiskorski.machinator.flow.parser;

import org.springframework.stereotype.Component;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.executor.ExecutionException;
import tpiskorski.machinator.model.vm.VirtualMachineState;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

@Component
public class ShowVmStateParser {

    public VirtualMachineState parse(CommandResult commandResult) {
        try {
            String std = commandResult.getStd();

            Properties properties = new Properties();
            properties.load(new StringReader(std));
            return getVmState(properties);
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    private VirtualMachineState getVmState(Properties properties) {
        try {
            return VirtualMachineState.parse(properties.getProperty("VMState"));
        } catch (Exception e) {
            return VirtualMachineState.COULD_NOT_PARSE;
        }
    }
}
