package tpiskorski.machinator.model.vm

import spock.lang.Specification
import spock.lang.Unroll

class VirtualMachineStateTest extends Specification {

    @Unroll
    def 'should parse string to vm state'() {
        expect:
        VirtualMachineState.parse(string) == expectedResult

        where:
        string             || expectedResult
        '"poweroff"'       || VirtualMachineState.POWEROFF
        '"running"'        || VirtualMachineState.RUNNING
        '"aborted"'        || VirtualMachineState.ABORTED
        '"saved"'          || VirtualMachineState.SAVED
        '"something else"' || VirtualMachineState.UNREACHABLE
    }
}


