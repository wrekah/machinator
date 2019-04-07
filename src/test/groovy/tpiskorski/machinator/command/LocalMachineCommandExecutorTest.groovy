package tpiskorski.machinator.command

import spock.lang.Specification
import spock.lang.Subject

class LocalMachineCommandExecutorTest extends Specification {

    def processExecutor = Mock(ProcessExecutor)
    def commandFactory = Mock(CommandResultFactory)

    @Subject executor = new LocalMachineCommandExecutor(
            processExecutor: processExecutor,
            commandResultFactory: commandFactory
    )

    def 'should execute the command by executing process and getting the results from the command'() {
        given:
        def command = Mock(Command)
        def process = Mock(Process)
        def commandResult = Mock(CommandResult)

        when:
        def result = executor.execute(command)

        then:
        1 * processExecutor.execute(command) >> process
        1 * commandFactory.from(process) >> commandResult

        and:
        result == commandResult
    }
}
