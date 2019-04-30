package tpiskorski.machinator.flow.executor

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.flow.command.CommandResultFactory

class LocalExecutorTest extends Specification {

    def processExecutor = Mock(ProcessExecutor)
    def commandResultFactory = Mock(CommandResultFactory)

    @Subject executor = new LocalExecutor(
            processExecutor: processExecutor,
            commandResultFactory: commandResultFactory
    )

    def 'should do local execute'() {
        given:
        def executionContext = Mock(ExecutionContext)
        when:
        executor.execute(executionContext)

        then:
        1 * processExecutor.execute(_, _)
        1 * commandResultFactory.from(_)
    }
}
