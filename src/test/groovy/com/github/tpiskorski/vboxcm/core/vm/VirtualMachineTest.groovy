package com.github.tpiskorski.vboxcm.core.vm

import spock.lang.Specification

class VirtualMachineTest extends Specification {

    def 'should create vm with unreachable state by default'() {
        given:
        def server = 'server'
        def id = 'id'

        def vm = new VirtualMachine(server, id)

        expect:
        vm.server == server
        vm.id == id
        vm.state == VirtualMachineState.UNREACHABLE
    }

    def 'should properly compare not equal vms by id'() {
        given:
        def vm1 = new VirtualMachine('server', 'id1')
        def vm2 = new VirtualMachine('server', 'id2')

        expect:
        vm1 != vm2

        and:
        vm2 != vm1
    }

    def 'should properly compare not equal vms by server'() {
        given:
        def vm1 = new VirtualMachine('server1', 'id')
        def vm2 = new VirtualMachine('server2', 'id')

        expect:
        vm1 != vm2

        and:
        vm2 != vm1
    }

    def 'should properly compare equal vms'() {
        given:
        def vm1 = new VirtualMachine('server', 'id')
        def vm2 = new VirtualMachine('server', 'id')

        expect:
        vm1 == vm2
        vm2 == vm1

        and:
        vm1.hashCode() == vm2.hashCode()
    }

    def 'should properly compare not server'() {
        given:
        def something = new Object()
        def vm = new VirtualMachine('server', 'id')

        expect:
        something != vm

        and:
        vm != something
    }
}
