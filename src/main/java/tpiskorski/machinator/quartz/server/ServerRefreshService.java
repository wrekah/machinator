package tpiskorski.machinator.quartz.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.action.CommandExecutor;
import tpiskorski.machinator.action.ExecutionContext;
import tpiskorski.machinator.command.*;
import tpiskorski.machinator.core.server.Server;
import tpiskorski.machinator.core.vm.VirtualMachine;

import java.io.IOException;
import java.util.List;

@Service
public class ServerRefreshService {

    @Autowired private CommandExecutor commandExecutor;
    @Autowired private CommandFactory commandFactory;

    @Autowired private VmDetailsService vmDetailsService;

    private SimpleVmParser simpleVmParser = new SimpleVmParser();

    public List<VirtualMachine> monitor(Server server) throws IOException, InterruptedException {
        ExecutionContext listAllVms = ExecutionContext.builder()
            .executeOn(server)
            .command(commandFactory.make(BaseCommand.LIST_ALL_VMS))
            .build();

        CommandResult result = commandExecutor.execute(listAllVms);
        List<VirtualMachine> vms = simpleVmParser.parse(result);
        vms.forEach(virtualMachine -> virtualMachine.setServer(server));
        vmDetailsService.enrichVms(vms);

        return vms;
    }
}
