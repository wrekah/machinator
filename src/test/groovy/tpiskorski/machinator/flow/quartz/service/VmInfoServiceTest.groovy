package tpiskorski.machinator.flow.quartz.service

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.flow.command.BaseCommand
import tpiskorski.machinator.flow.command.CommandFactory
import tpiskorski.machinator.flow.command.CommandResult
import tpiskorski.machinator.flow.executor.CommandExecutor
import tpiskorski.machinator.flow.executor.ExecutionException
import tpiskorski.machinator.flow.parser.ShowVmInfoParser
import tpiskorski.machinator.flow.parser.ShowVmStateParser
import tpiskorski.machinator.model.server.Server
import tpiskorski.machinator.model.server.ServerType
import tpiskorski.machinator.model.vm.VirtualMachine

class VmInfoServiceTest extends Specification {

    def commandExecutor = Mock(CommandExecutor)
    def commandFactory = Mock(CommandFactory)
    def showVmInfoParser = Mock(ShowVmInfoParser)
    def showVmStateParser = Mock(ShowVmStateParser)

    @Subject service = new VmInfoService(commandExecutor, commandFactory, showVmInfoParser, showVmStateParser)

    def 'should get info on vm'() {
        given:
        def vm = Mock(VirtualMachine) {
            getId() >> 'vm id'
            getServer() >> Mock(Server) {
                getServerType() >> ServerType.LOCAL
            }
        }

        when:
        service.info(vm)

        then:
        1 * commandFactory.makeWithArgs(BaseCommand.SHOW_VM_INFO, 'vm id')
        1 * commandExecutor.execute(_) >> Mock(CommandResult) { isFailed() >> false }

        and:
        notThrown(ExecutionException)
    }
}
