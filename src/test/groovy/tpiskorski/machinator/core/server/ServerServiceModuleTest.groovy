package tpiskorski.machinator.core.server

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.core.vm.VirtualMachine
import tpiskorski.machinator.core.vm.VirtualMachineRepository
import tpiskorski.machinator.core.vm.VirtualMachineService
import tpiskorski.machinator.core.vm.VirtualMachineState

class ServerServiceModuleTest extends Specification {

    def serverRepository = new ServerRepository()
    def virtualMachineRepository = new VirtualMachineRepository()
    def virtualMachineService = new VirtualMachineService(virtualMachineRepository)

    @Subject service = new ServerService(serverRepository, virtualMachineService)

    def server1 = new Server('some', '123')
    def server2 = new Server('some', '321')

    def 'should get no servers if nothing was added'() {
        expect:
        service.getServers().empty
    }

    def 'should get servers that were added'() {
        when:
        service.add(server1)
        service.add(server2)

        then:
        service.getServers() == [server1, server2]
    }

    def 'should properly remove servers'() {
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
        def vm1 = new VirtualMachine(server1, 'vm1')
        def vm2 = new VirtualMachine(server1, 'vm2')
        def vm3 = new VirtualMachine(server2, 'v1')

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
        def vm1 = new VirtualMachine(server1, 'vm1')
        def vm2 = new VirtualMachine(server1, 'vm2')
        def vm3 = new VirtualMachine(server2, 'v1')

        and:
        service.add(server1)
        service.add(server2)

        and:
        virtualMachineService.add(vm1)
        virtualMachineService.add(vm2)
        virtualMachineService.add(vm3)

        and:
        def newVm1 = new VirtualMachine(server1, 'some new vm')

        when:
        service.updateReachable(server1, [newVm1])

        then:
        server1.serverState == ServerState.REACHABLE

        and:
        virtualMachineService.getVms(server1).contains(newVm1)
        virtualMachineService.getVms().contains(newVm1)
    }

    def 'should check if contains server'() {
        given:

        when:
        service.add(server1)

        then:
        service.contains(server1)
        !service.contains(server2)

        when:
        service.remove(server1)

        then:
        !service.contains(server1)
        !service.contains(server2)
    }
}
