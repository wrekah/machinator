package tpiskorski.machinator.command

import spock.lang.Specification
import spock.lang.Subject

class SimpleVmParserTest extends Specification {

    @Subject parser = new SimpleVmParser()

    def 'should parse empty command result and return empty list'() {
        given:
        def commandResult = Mock(CommandResult)
        commandResult.getStd() >> ''

        when:
        def result = parser.parse(commandResult)

        then:
        result.empty
    }

    def 'should parse command result to vms'() {
        given:
        def commandResult = Mock(CommandResult)
        commandResult.getStd() >> '''"vm1" {shortId}
"vm2" {762d68aa}
"vm3" {31abfe1b-6a83-49f5}'''

        when:
        def result = parser.parse(commandResult)

        then:
        result.size() == 3
        result*.vmName == ['vm1', 'vm2', 'vm3']
        result*.id == ['shortId', '762d68aa', '31abfe1b-6a83-49f5']
    }
}
