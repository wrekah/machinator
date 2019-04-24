package tpiskorski.machinator.command

import org.apache.commons.io.IOUtils
import spock.lang.Specification
import spock.lang.Subject

class CommandResultFactoryTest extends Specification {

    @Subject factory = new CommandResultFactory()

    def 'should create empty command result if process is null'() {
        when:
        def result = factory.from(null)

        then:
        result.std.empty
        result.error.empty
        result.failed
        !result.success
    }

    def 'should create command result from process'() {
        given:
        def inputStream = IOUtils.toInputStream('', 'UTF-8')
        def errorStream = IOUtils.toInputStream('', 'UTF-8')

        def process = Mock(Process)

        when:
        def result = factory.from(process)

        then:
        1 * process.getInputStream() >> inputStream
        1 * process.getErrorStream() >> errorStream

        and:
        result.std.empty
        result.error.empty
        !result.failed
        result.success
    }

    def 'should create command result from failed process'() {
        given:
        def errorMsg = 'This command failed.\nTry once again.'
        def inputStream = IOUtils.toInputStream('', 'UTF-8')
        def errorStream = IOUtils.toInputStream(errorMsg, 'UTF-8')

        def process = Mock(Process)

        when:
        def result = factory.from(process)

        then:
        1 * process.getInputStream() >> inputStream
        1 * process.getErrorStream() >> errorStream

        and:
        result.std.empty
        result.error == errorMsg
        result.failed
        !result.success
    }

    def 'should create success result from process'() {
        given:
        def msg = 'Some commnad has been executed.\nResults are following:\nabc'
        def inputStream = IOUtils.toInputStream(msg, 'UTF-8')
        def errorStream = IOUtils.toInputStream('', 'UTF-8')

        def process = Mock(Process)

        when:
        def result = factory.from(process)

        then:
        1 * process.getInputStream() >> inputStream
        1 * process.getErrorStream() >> errorStream

        and:
        result.std == msg
        result.error.empty
        !result.failed
        result.success
    }
}
