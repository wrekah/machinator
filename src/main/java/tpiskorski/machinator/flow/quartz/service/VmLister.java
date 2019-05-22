package tpiskorski.machinator.flow.quartz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.command.BaseCommand;
import tpiskorski.machinator.flow.command.CommandFactory;
import tpiskorski.machinator.flow.command.CommandResult;
import tpiskorski.machinator.flow.command.VmDetailsService;
import tpiskorski.machinator.flow.executor.CommandExecutor;
import tpiskorski.machinator.flow.executor.ExecutionContext;
import tpiskorski.machinator.flow.parser.SimpleVmParser;
import tpiskorski.machinator.model.server.Server;
import tpiskorski.machinator.model.vm.VirtualMachine;

import java.util.List;

@Service
public class VmLister {

    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    private SimpleVmParser simpleVmParser = new SimpleVmParser();
    @Autowired private VmDetailsService vmDetailsService;

    @Autowired
    public VmLister(CommandExecutor commandExecutor, CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    public List<VirtualMachine> list(Server server) {
        ExecutionContext listAllVms = ExecutionContext.builder()
            .executeOn(server)
            .command(commandFactory.make(BaseCommand.LIST_ALL_VMS))
            .build();

        CommandResult result = commandExecutor.execute(listAllVms);

        if (result.isFailed()) {
            throw new RuntimeException(result.getError());
        }

        List<VirtualMachine> vms = simpleVmParser.parse(result);
        vms.forEach(virtualMachine -> virtualMachine.setServer(server));
        vmDetailsService.enrichVms(vms);

        return vms;
    }
}
