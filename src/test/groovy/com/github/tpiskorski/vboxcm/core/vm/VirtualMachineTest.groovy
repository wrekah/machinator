package com.github.tpiskorski.vboxcm.core.vm

import com.github.tpiskorski.vboxcm.core.server.Server
import spock.lang.Specification

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
}
