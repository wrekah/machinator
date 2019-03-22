package com.github.tpiskorski.vboxcm.core.vm

import com.github.tpiskorski.vboxcm.core.server.Server
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class VirtualMachineRepositoryTest extends Specification {

    @Subject repository = new VirtualMachineRepository()

    def 'should add vms'() {
        given:
        def vm1 = new VirtualMachine('server1', 'id1')
        def vm2 = new VirtualMachine('server2', 'id1')
        def vm3 = new VirtualMachine('server1', 'id2')

        when:
        repository.add(vm1)
        repository.add(vm2)
        repository.add(vm3)

        then:
        repository.getVms() == [vm1, vm2, vm3]
    }

    def 'should remove vm'() {
        given:
        def vm = new VirtualMachine('server1', 'id1')

        when:
        repository.add(vm)
        repository.remove(vm)

        then:
        repository.getVms().empty
    }

    def 'should add and remove vms'() {
        given:
        def vm1 = new VirtualMachine('server1', 'id1')
        def vm2 = new VirtualMachine('server2', 'id1')
        def vm3 = new VirtualMachine('server1', 'id2')

        when:
        repository.add(vm1)
        repository.add(vm2)
        repository.add(vm3)

        then:
        repository.getVms() == [vm1, vm2, vm3]

        when:
        repository.remove(vm1)

        then:
        repository.getVms() == [vm2, vm3]

        when:
        repository.remove(vm2)

        then:
        repository.getVms() == [vm3]

        when:
        repository.remove(vm3)

        then:
        repository.getVms().empty
    }

    @Unroll
    def 'should get vms by server'() {
        given:
        def vm1 = new VirtualMachine('server1', 'id1')
        def vm2 = new VirtualMachine('server2', 'id1')
        def vm3 = new VirtualMachine('server1', 'id2')

        and:
        repository.add(vm1)
        repository.add(vm2)
        repository.add(vm3)

        expect:
        repository.getVms(new Server(serverAddress)).size() == expectedSize

        where:
        serverAddress  || expectedSize
        'server1'      || 2
        'server2'      || 1
        'non existent' || 0
    }

    @Unroll
    def 'should remove vms by server'() {
        given:
        def vm1 = new VirtualMachine('server1', 'id1')
        def vm2 = new VirtualMachine('server2', 'id1')
        def vm3 = new VirtualMachine('server1', 'id2')

        and:
        repository.add(vm1)
        repository.add(vm2)
        repository.add(vm3)

        and:
        def server = new Server(serverAddress)

        when:
        repository.removeByServer(server)

        then:
        repository.getVms(server).size() == 0
        repository.getVms().size() == expectedSize

        where:
        serverAddress  || expectedSize
        'server1'      || 1
        'server2'      || 2
        'non existent' || 3
    }
}
