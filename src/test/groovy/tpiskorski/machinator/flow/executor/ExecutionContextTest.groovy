package tpiskorski.machinator.flow.executor

import spock.lang.Specification
import tpiskorski.machinator.flow.command.Command
import tpiskorski.machinator.flow.executor.ExecutionContext
import tpiskorski.machinator.model.server.Credentials
import tpiskorski.machinator.model.server.Server

class ExecutionContextTest extends Specification {

    def 'should build execution command context for local execution'() {
        given:
        def server = new Server('local', '')
        def command = Mock(Command)
        def workingDir = Mock(File)

        def executionContext = ExecutionContext.builder()
                .executeOn(server)
                .command(command)
                .workingDirectory(workingDir)
                .build()

        expect:
        executionContext.local
        executionContext.command == command
        executionContext.workingDirectory == workingDir
    }

    def 'should build execution command context for remote execution'() {
        given:
        def server = new Server(new Credentials('user', 'pw'), 'address', '123')
        def command = Mock(Command)
        def workingDir = Mock(File)

        def executionContext = ExecutionContext.builder()
                .executeOn(server)
                .command(command)
                .workingDirectory(workingDir)
                .build()

        expect:
        !executionContext.local
        executionContext.command == command
        executionContext.workingDirectory == workingDir
        executionContext.user == 'user'
        executionContext.password == 'pw'
        executionContext.address == 'address'
        executionContext.port == 123
    }
}
