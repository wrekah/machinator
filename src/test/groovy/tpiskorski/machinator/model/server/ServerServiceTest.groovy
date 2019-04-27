package tpiskorski.machinator.model.server

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.model.vm.VirtualMachine
import tpiskorski.machinator.model.vm.VirtualMachineService

class ServerServiceTest extends Specification {

    def serverRepository = Mock(ServerRepository)
    def virtualMachineService = Mock(VirtualMachineService)

    @Subject service = new ServerService(serverRepository, virtualMachineService)

    def 'should get servers from repository'() {
        when:
        service.getServers()

        then:
        1 * serverRepository.getServersList()
    }

    def 'should add server'() {
        given:
        def server = Mock(Server)

        when:
        service.add(server)

        then:
        1 * serverRepository.add(server)
    }

    def 'should remove server and corresponding vms'() {
        given:
        def server = Mock(Server)

        when:
        service.remove(server)

        then:
        1 * serverRepository.remove(server)
        1 * virtualMachineService.removeByServer(server)
    }

    def 'should update unreachable to server and vms'() {
        given:
        def server = Mock(Server)

        when:
        service.updateUnreachable(server)

        then:
        1 * server.setServerState(ServerState.NOT_REACHABLE)
        1 * virtualMachineService.updateNotReachableBy(server)
    }

    def 'should update reachable to server and vms'() {
        given:
        def server = Mock(Server)
        def vms = [Mock(VirtualMachine)]

        when:
        service.updateReachable(server, vms)

        then:
        1 * server.setServerState(ServerState.REACHABLE)
        1 * virtualMachineService.replace(server, vms)
    }

    def 'should check if it contains server'() {
        given:
        def server = Mock(Server)

        when:
        service.contains(server)

        then:
        1 * serverRepository.contains(server)
    }
}
