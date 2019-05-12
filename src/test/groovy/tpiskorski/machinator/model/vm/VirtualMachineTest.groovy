package tpiskorski.machinator.model.vm

import spock.lang.Specification
import tpiskorski.machinator.model.server.Server

class VirtualMachineTest extends Specification {

    def server1 = new Server('some', '123')
    def server2 = new Server('other', '321')

    def 'should create vm with unreachable state by default'() {
        given:
        def id = 'id'
        def vm = new VirtualMachine(server1, id)

        expect:
        vm.server == server1
        vm.id == id
        vm.state == VirtualMachineState.UNREACHABLE
    }

    def 'should properly compare not equal vms by id'() {
        given:
        def vm1 = new VirtualMachine(server1, 'id1')
        def vm2 = new VirtualMachine(server1, 'id2')

        expect:
        vm1 != vm2

        and:
        vm2 != vm1
    }

    def 'should properly compare not equal vms by server'() {
        given:
        def vm1 = new VirtualMachine(server1, 'id')
        def vm2 = new VirtualMachine(server2, 'id')

        expect:
        vm1 != vm2

        and:
        vm2 != vm1
    }

    def 'should properly compare equal vms'() {
        given:
        def vm1 = new VirtualMachine(server1, 'id')
        def vm2 = new VirtualMachine(server1, 'id')

        expect:
        vm1 == vm2
        vm2 == vm1

        and:
        vm1.hashCode() == vm2.hashCode()
    }

    def 'should properly compare not server'() {
        given:
        def something = new Object()
        def vm = new VirtualMachine(server1, 'id')

        expect:
        something != vm

        and:
        vm != something
    }

    def 'should not be equal if all fields are compared'() {
        given:
        def vm1 = new VirtualMachine(server1, 'id')
        vm1.cpuCores = 1
        vm1.ramMemory = 1024
        vm1.state = VirtualMachineState.POWEROFF

        and:
        def vm2 = new VirtualMachine(server1, 'id')
        vm2.cpuCores = 1
        vm2.ramMemory = 1024
        vm2.state = VirtualMachineState.RUNNING

        expect:
        !vm1.deepEquals(vm2)
    }

    def 'should equal if all fields are compared'() {
        given:
        def vm1 = new VirtualMachine(server1, 'id')
        vm1.cpuCores = 1
        vm1.ramMemory = 1024
        vm1.state = VirtualMachineState.POWEROFF

        and:
        def vm2 = new VirtualMachine(server1, 'id')
        vm2.cpuCores = 1
        vm2.ramMemory = 1024
        vm2.state = VirtualMachineState.POWEROFF

        expect:
        vm1.deepEquals(vm2)
    }
}
