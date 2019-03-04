package com.github.tpiskorski.vboxcm.core.vm

import spock.lang.Specification
import spock.lang.Subject

class VirtualMachineServiceTest extends Specification {

    def virtualMachineRepository = new VirtualMachineRepository()

    @Subject service = new VirtualMachineService(virtualMachineRepository)

    def 'should get no vms if nothing was added'() {
        expect:
        service.getVms().empty
    }

    def 'should get vms that were added'() {
        given:
        def vm1 = new VirtualMachine('server1', 'vm1')
        def vm2 = new VirtualMachine('server1', 'vm2')

        when:
        service.add(vm1)
        service.add(vm2)

        then:
        service.getVms() == [vm1, vm2]
    }

    def 'should properly remove vms'() {
        given:
        def vm1 = new VirtualMachine('server1', 'vm1')
        def vm2 = new VirtualMachine('server1', 'vm2')
        def vm3 = new VirtualMachine('server2', 'vm1')

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
        given:
        def vm1 = new VirtualMachine('server1', 'vm1')
        def vm2 = new VirtualMachine('server1', 'vm2')

        when:
        service.add(vm1)

        then:
        service.getVms() == [vm1]

        when:
        service.remove(vm2)

        then:
        service.getVms() == [vm1]
    }
}



 

 


 