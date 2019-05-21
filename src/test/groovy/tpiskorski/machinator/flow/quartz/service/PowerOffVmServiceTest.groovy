package tpiskorski.machinator.flow.quartz.service

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.flow.command.BaseCommand
import tpiskorski.machinator.flow.command.CommandFactory
import tpiskorski.machinator.flow.command.CommandResult
import tpiskorski.machinator.flow.executor.CommandExecutor
import tpiskorski.machinator.flow.executor.ExecutionException
import tpiskorski.machinator.flow.parser.ProgressCommandsInterpreter
import tpiskorski.machinator.model.server.Server
import tpiskorski.machinator.model.server.ServerType
import tpiskorski.machinator.model.vm.VirtualMachine

class PowerOffVmServiceTest extends Specification {

    def commandExecutor = Mock(CommandExecutor)
    def commandFactory = Mock(CommandFactory)
    def progressCommandsInterpreter = Mock(ProgressCommandsInterpreter)

    @Subject service = new PowerOffVmService(commandExecutor, commandFactory, progressCommandsInterpreter)

    def 'should power off vm'() {
        given:
        def vm = Mock(VirtualMachine) {
            getVmName() >> 'vm name'
            getServer() >> Mock(Server) {
                getServerType() >> ServerType.LOCAL
            }
        }

        when:
        service.powerOff(vm)

        then:
        1 * commandFactory.makeWithArgs(BaseCommand.TURN_OFF, 'vm name')
        1 * commandExecutor.execute(_) >> Mock(CommandResult)
        1 * progressCommandsInterpreter.isSuccess(_) >> true

        and:
        notThrown(ExecutionException)
    }

    def 'should thrown execution exception if powering off vm failed'() {
        given:
        def vm = Mock(VirtualMachine) {
            getVmName() >> 'vm name'
            getServer() >> Mock(Server) {
                getServerType() >> ServerType.LOCAL
            }
        }

        when:
        service.powerOff(vm)

        then:
        1 * commandFactory.makeWithArgs(BaseCommand.TURN_OFF, 'vm name')
        1 * commandExecutor.execute(_) >> Mock(CommandResult)
        1 * progressCommandsInterpreter.isSuccess(_) >> false

        and:
        thrown(ExecutionException)
    }
}
