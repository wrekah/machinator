package com.github.tpiskorski.vboxcm.core.vm

import spock.lang.Specification

class VirtualMachineTest extends Specification {

    def 'should create vm with unreachable state by default'() {
        given:
        def server = 'server'
        def vmName = 'some vm name'

        def vm = new VirtualMachine(server, vmName)

        expect:
        vm.server == server
        vm.vmName == vmName
        vm.state == VirtualMachineState.UNREACHABLE
    }

    def 'should properly compare not equal vms by vm'() {
        given:
        def vm1 = new VirtualMachine('server', 'vm1')
        def vm2 = new VirtualMachine('server', 'vm2')

        expect:
        vm1 != vm2

        and:
        vm2 != vm1
    }

    def 'should properly compare not equal vms by server'() {
        given:
        def vm1 = new VirtualMachine('server1', 'vm')
        def vm2 = new VirtualMachine('server2', 'vm')

        expect:
        vm1 != vm2

        and:
        vm2 != vm1
    }

    def 'should properly compare equal vms'() {
        given:
        def vm1 = new VirtualMachine('server', 'vm')
        def vm2 = new VirtualMachine('server', 'vm')

        expect:
        vm1 == vm2
        vm2 == vm1

        and:
        vm1.hashCode() == vm2.hashCode()
    }

    def 'should properly compare not server'() {
        given:
        def something = new Object()
        def vm = new VirtualMachine('server', 'vm')

        expect:
        something != vm

        and:
        vm != something
    }
}
