package tpiskorski.vboxcm.core.vm

import tpiskorski.vboxcm.core.server.Server
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class VirtualMachineServiceModuleTest extends Specification {

    def virtualMachineRepository = new VirtualMachineRepository()

    @Subject service = new VirtualMachineService(virtualMachineRepository)

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
        service.getVms().empty
    }

    def 'should get vms that were added'() {
        when:
        service.add(vm1)
        service.add(vm2)

        then:
        service.getVms() == [vm1, vm2]
    }

    def 'should properly remove vms'() {
        when:
        service.add(vm1)
        service.add(vm2)
        service.add(vm3)

        then:
        service.getVms() == [vm1, vm2, vm3]

        when:
        service.remove(vm1)

        then:
        service.getVms() == [vm2, vm3]

        when:
        service.remove(vm2)

        then:
        service.getVms() == [vm3]

        when:
        service.remove(vm3)

        then:
        service.getVms().empty
    }

    def 'should not remove the vm that is not present'() {
        when:
        service.add(vm1)

        then:
        service.getVms() == [vm1]

        when:
        service.remove(vm2)

        then:
        service.getVms() == [vm1]
    }

    @Unroll
    def 'should get vms by server'() {
        given:
        service.add(vm1)
        service.add(vm2)
        service.add(vm3)

        expect:
        service.getVms(targetServer).size() == expectedSize

        where:
        targetServer        || expectedSize
        server1             || 2
        server2             || 1
        non_existent_server || 0
    }

    def 'should add vm when updating if no such a vm is present '() {
        given:
        service.add(vm1)

        when:
        service.upsert(vm2)

        then:
        service.getVms() == [vm1, vm2]
    }

    def 'should update vm'() {
        given:
        vm1.cpuCores = 1
        vm1.ramMemory = 1024
        vm1.state = VirtualMachineState.OFF
        vm1.vmName = 'vm1'

        and:
        def vmUpdate = new VirtualMachine(vm1.server, vm1.id)
        vmUpdate.cpuCores = 4
        vmUpdate.ramMemory = 2048
        vmUpdate.state = VirtualMachineState.ON
        vmUpdate.vmName = 'vm2'

        when:
        service.add(vm1)

        and:
        service.upsert(vmUpdate)

        then:
        service.getVms().first() == vm1
        vm1.cpuCores == 4
        vm1.ramMemory == 2048
        vm1.state == VirtualMachineState.ON
        vm1.vmName == 'vm2'
    }
}



 

 


 