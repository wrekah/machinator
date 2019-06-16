package tpiskorski.machinator.model.server

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.lifecycle.quartz.PersistScheduler
import tpiskorski.machinator.lifecycle.state.manager.PersistenceType
import tpiskorski.machinator.model.vm.VirtualMachine
import tpiskorski.machinator.model.vm.VirtualMachineService

class ServerServiceTest extends Specification {

    def serverRepository = Mock(ServerRepository)
    def virtualMachineService = Mock(VirtualMachineService)
    def persistScheduler = Mock(PersistScheduler)

    @Subject service = new ServerService(serverRepository, virtualMachineService, persistScheduler)

    def 'should get servers from repository'() {
        when:
        service.getServers()

        then:
        1 * serverRepository.getServersList()
    }

    def 'should add server and schedule persistence'() {
        given:
        def server = Mock(Server)

        when:
        service.add(server)

        then:
        1 * serverRepository.add(server)
        1 * persistScheduler.schedulePersistence(PersistenceType.SERVER)
    }

    def 'should put server without scheduling persistence'() {
        given:
        def server = Mock(Server)

        when:
        service.put(server)

        then:
        1 * serverRepository.add(server)
        0 * persistScheduler.schedulePersistence(PersistenceType.SERVER)
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

    def 'should check if it contains server'() {
        given:
        def server = Mock(Server)

        when:
        service.contains(server)

        then:
        1 * serverRepository.contains(server)
    }

    def 'should perform refresh'() {
        given:
        def server = Mock(Server)
        def vms = [Mock(VirtualMachine)]

        when:
        service.refresh(server, vms)

        then:
        1 * server.setServerState(ServerState.REACHABLE)
        1 * virtualMachineService.refresh(server, vms)
    }

    def 'should update unreachable'() {
        given:
        def server = Mock(Server)

        when:
        service.unreachable(server)

        then:
        1 * server.setServerState(ServerState.NOT_REACHABLE)
        1 * virtualMachineService.unreachable(server)
    }
}
