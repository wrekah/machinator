package tpiskorski.machinator.flow.quartz.service

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.flow.command.BaseCommand
import tpiskorski.machinator.flow.command.CommandFactory
import tpiskorski.machinator.flow.command.CommandResult
import tpiskorski.machinator.flow.executor.CommandExecutor
import tpiskorski.machinator.flow.executor.ExecutionException
import tpiskorski.machinator.model.server.Server
import tpiskorski.machinator.model.server.ServerType
import tpiskorski.machinator.model.vm.VirtualMachine

class StartVmServiceTest extends Specification {
    def commandExecutor = Mock(CommandExecutor)
    def commandFactory = Mock(CommandFactory)

    @Subject service = new StartVmService(commandExecutor, commandFactory)

    def 'should start vm'() {
        given:
        def vm = Mock(VirtualMachine) {
            getVmName() >> 'vm name'
            getServer() >> Mock(Server) {
                getServerType() >> ServerType.LOCAL
            }
        }

        when:
        service.start(vm)

        then:
        1 * commandFactory.makeWithArgs(BaseCommand.START_VM, 'vm name')
        1 * commandExecutor.execute(_) >> Mock(CommandResult) { isFailed() >> false }

        and:
        notThrown(ExecutionException)
    }

    def 'should thrown execution exception if starting vm failed'() {
        given:
        def vm = Mock(VirtualMachine) {
            getVmName() >> 'vm name'
            getServer() >> Mock(Server) {
                getServerType() >> ServerType.LOCAL
            }
        }

        when:
        service.start(vm)

        then:
        1 * commandFactory.makeWithArgs(BaseCommand.START_VM, 'vm name')
        1 * commandExecutor.execute(_) >> Mock(CommandResult) { isFailed() >> true }

        and:
        thrown(ExecutionException)
    }
}
