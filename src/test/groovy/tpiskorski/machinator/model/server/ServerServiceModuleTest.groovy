package tpiskorski.machinator.model.server

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.model.vm.VirtualMachine
import tpiskorski.machinator.model.vm.VirtualMachineRepository
import tpiskorski.machinator.model.vm.VirtualMachineService

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

    def 'should check if contains server'() {
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

    def 'should perform refresh'() {
        given:
        def vm1 = new VirtualMachine(server1, 'vm1')
        def vm2 = new VirtualMachine(server1, 'vm2')
        def vm3 = new VirtualMachine(server1, 'v1')

        when:
        service.refresh(server1, [vm1, vm2, vm3])

        then:
        virtualMachineService.getVms(server1).size() == 3

        when:
        service.refresh(server1, [])

        then:
        virtualMachineService.getVms(server1).empty
    }
}
