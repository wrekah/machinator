package tpiskorski.machinator.flow.parser

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import tpiskorski.machinator.flow.command.CommandResult
import tpiskorski.machinator.model.vm.VirtualMachineState

class ShowVmStateParserTest extends Specification {

    @Subject parser = new ShowVmStateParser()

    @Unroll
    def 'should parse showvminfo command'() {
        given:
        def commandResult = Mock(CommandResult)
        commandResult.getStd() >> """
VMState=$vmState
"""

        expect:
        parser.parse(commandResult) == expected

        where:
        vmState      || expected
        '"poweroff"' || VirtualMachineState.POWEROFF
        '"running"'  || VirtualMachineState.RUNNING
    }
}
