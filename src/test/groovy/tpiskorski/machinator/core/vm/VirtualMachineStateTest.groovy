package tpiskorski.machinator.core.vm

import spock.lang.Specification
import spock.lang.Unroll

class VirtualMachineStateTest extends Specification {

    @Unroll
    def 'should parse string to vm state'() {
        expect:
        VirtualMachineState.parse(string) == expectedResult

        where:
        string           || expectedResult
        "\"poweroff\""   || VirtualMachineState.OFF
        "something else" || VirtualMachineState.UNREACHABLE
    }
}

