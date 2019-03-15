package com.github.tpiskorski.vboxcm.core.server

import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineRepository
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineService
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineState
import spock.lang.Specification
import spock.lang.Subject

class ServerServiceModuleTest extends Specification {

    def serverRepository = new ServerRepository()
    def virtualMachineRepository = new VirtualMachineRepository()
    def virtualMachineService = new VirtualMachineService(virtualMachineRepository)

    @Subject service = new ServerService(serverRepository, virtualMachineService)

    def 'should get no servers if nothing was added'() {
        expect:
        service.getServers().empty
    }

    def 'should get servers that were added'() {
        given:
        def server1 = new Server('some:address')
        def server2 = new Server('some:otheraddress')

        when:
        service.add(server1)
        service.add(server2)

        then:
        service.getServers() == [server1, server2]
    }

    def 'should properly remove servers'() {
        given:
        def server1 = new Server('some:address')
        def server2 = new Server('some:otheraddress')

        when:
        service.add(server1)
        service.add(server2)

        then:
        service.getServers() == [server1, server2]

        when:
        service.remove(server1)

        then:
        service.getServers() == [server2]

        when:
        service.remove(server2)

        then:
        service.getServers().empty
    }

    def 'should not remove the server that is not present'() {
        given:
        def server1 = new Server('some:address')
        def server2 = new Server('some:otheraddress')

        when:
        service.add(server1)

        then:
        service.getServers() == [server1]

        when:
        service.remove(server2)

        then:
        service.getServers() == [server1]
    }

    def 'should update not_reachable state to the server and vms'() {
        given:
        def server1 = new Server('some:address')
        def server2 = new Server('some:otheraddress')

        def vm1 = new VirtualMachine('some:address', 'vm1')
        def vm2 = new VirtualMachine('some:address', 'vm2')
        def vm3 = new VirtualMachine('some:otheraddress', 'v1')

        and:
        service.add(server1)
        service.add(server2)

        and:
        virtualMachineService.add(vm1)
        virtualMachineService.add(vm2)
        virtualMachineService.add(vm3)

        when:
        service.updateUnreachable(server1)

        then:
        server1.serverState == ServerState.NOT_REACHABLE
        vm1.state == VirtualMachineState.UNREACHABLE
        vm2.state == VirtualMachineState.UNREACHABLE
    }

    def 'should update reachable state to the server and vms'() {
        given:
        def server1 = new Server('some:address')
        def server2 = new Server('some:otheraddress')

        def vm1 = new VirtualMachine('some:address', 'vm1')
        def vm2 = new VirtualMachine('some:address', 'vm2')
        def vm3 = new VirtualMachine('some:otheraddress', 'v1')

        and:
        service.add(server1)
        service.add(server2)

        and:
        virtualMachineService.add(vm1)
        virtualMachineService.add(vm2)
        virtualMachineService.add(vm3)

        and:
        def newVm1 = new VirtualMachine('some:address', 'some new vm')

        when:
        service.updateReachable(server1, [newVm1])

        then:
        server1.serverState == ServerState.REACHABLE

        and:
        virtualMachineService.getVms(server1).contains(newVm1)
        virtualMachineService.getVms().contains(newVm1)
    }
}
