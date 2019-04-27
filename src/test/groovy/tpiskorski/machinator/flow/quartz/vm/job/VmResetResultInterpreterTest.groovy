package tpiskorski.machinator.flow.quartz.vm.job

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.flow.parser.VmResetResultInterpreter

class VmResetResultInterpreterTest extends Specification {

    @Subject interpreter = new VmResetResultInterpreter()

    def 'should properly interpret successfully executed command'() {
        given:
        def result = Mock(CommandResult) {
            getStd() >> ""
            getError() >> ""
        }

        expect:
        interpreter.isSuccess(result)
    }

    def 'should properly interpret the result when it failed'() {
        given:
        def result = Mock(CommandResult) {
            getStd() >> ""
            getError() >> "VBoxManage: error: Machine 'vm' is not currently running"
        }

        expect:
        !interpreter.isSuccess(result)
    }
}
