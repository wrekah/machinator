package tpiskorski.machinator.model.vm

import tpiskorski.machinator.model.server.Server
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class VirtualMachineRepositoryTest extends Specification {

    @Subject repository = new VirtualMachineRepository()

    @Shared Server server1, server2, non_existent_server
    @Shared VirtualMachine vm1, vm2, vm3

    def setup() {
        server1 = new Server('some', '123')
        server2 = new Server('other', '321')

        non_existent_server = new Server('not', 'existent')

        vm1 = new VirtualMachine(server1, 'id1')
        vm2 = new VirtualMachine(server2, 'id1')
        vm3 = new VirtualMachine(server1, 'id2')
    }

    def 'should get no vms if nothing was added'() {
        expect:
        repository.getVms().empty
    }

    def 'should add vms'() {
        when:
        repository.add(vm1)
        repository.add(vm2)
        repository.add(vm3)

        then:
        repository.getVms() == [vm1, vm2, vm3]
    }

    def 'should remove vm'() {
        when:
        repository.add(vm1)
        repository.remove(vm1)

        then:
        repository.getVms().empty
    }

    def 'should add and remove vms'() {
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
        repository.add(vm1)
        repository.add(vm2)
        repository.add(vm3)

        expect:
        repository.getVms(serverAddress).size() == expectedSize

        where:
        serverAddress       || expectedSize
        server1             || 2
        server2             || 1
        non_existent_server || 0
    }

    @Unroll
    def 'should remove vms by server'() {
        given:
        def vm1 = new VirtualMachine(server1, 'id1')
        def vm2 = new VirtualMachine(server2, 'id1')
        def vm3 = new VirtualMachine(server1, 'id2')

        and:
        repository.add(vm1)
        repository.add(vm2)
        repository.add(vm3)

        when:
        repository.removeByServer(targetServer)

        then:
        repository.getVms(targetServer).size() == 0
        repository.getVms().size() == expectedSize

        where:
        targetServer        || expectedSize
        server1             || 1
        server2             || 2
        non_existent_server || 3
    }

    def 'should find virtual machine that is present'() {
        given:
        repository.add(vm1)

        when:
        def result = repository.find(vm1)

        then:
        result.isPresent()
        result.get() == vm1
    }

    def 'should not find if no virtual machine is not present'() {
        given:
        repository.add(vm1)

        when:
        def result = repository.find(vm2)

        then:
        result.isEmpty()
    }
}
