package tpiskorski.machinator.action

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.core.server.Credentials
import tpiskorski.machinator.core.server.Server

class CommandExecutorTest extends Specification {

    def localExecutor = Mock(LocalExecutor)
    def remoteExecutor = Mock(RemoteExecutor)

    @Subject executor = new CommandExecutor(localExecutor, remoteExecutor)

    def 'should delegate local command to local executor'() {
        given:
        def server = new Server('Local Machine', '')
        def executionContext = ExecutionContext.builder()
                .executeOn(server)
                .build()

        when:
        executor.execute(executionContext)

        then:
        1 * localExecutor.execute(executionContext)
        0 * remoteExecutor.execute(executionContext)
    }

    def 'should delegate remote command to local executor'() {
        given:
        def server = new Server(new Credentials('user', 'pw'), 'address', '123')
        def executionContext = ExecutionContext.builder()
                .executeOn(server)
                .build()

        when:
        executor.execute(executionContext)

        then:
        1 * remoteExecutor.execute(executionContext)
        0 * localExecutor.execute(executionContext)
    }
}
