package tpiskorski.machinator.flow.quartz.backup

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.flow.parser.ExportVmResultInterpreter

class ExportVmResultInterpreterTest extends Specification {

    @Subject interpreter = new ExportVmResultInterpreter()

    def 'should interpret successfully executed command'() {
        given:
        def result = Mock(CommandResult) {
            getStd() >> 'Successfully exported 1 machine(s).'
            getError() >> '0%...10%...20%...30%...40%...50%...60%...70%...80%...90%...100%'
        }

        expect:
        interpreter.isSuccess(result)
    }

    def 'should interpret command that executed with error'() {
        given:
        def result = Mock(CommandResult) {
            getStd() >> ''
            getError() >> 'Progress state: VBOX_E_IPRT_ERROR'
        }

        expect:
        !interpreter.isSuccess(result)
    }
}
