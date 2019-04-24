package tpiskorski.machinator.core.server

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.core.vm.VirtualMachine

class AddRemoteServerServiceTest extends Specification {

    def serverService = Mock(ServerService)

    @Subject addRemoteServerService = new AddRemoteServerService(
            serverService: serverService
    )

    def 'should create action that does server and vms update'() {
        given:
        def server = Mock(Server)
        def vms = [Mock(VirtualMachine)]
        def action = addRemoteServerService.addServerAndVmsAction(server, vms)

        when:
        action.perform()

        then:
        1 * serverService.add(server)
        1 * serverService.upsert(server, vms)
    }
}
