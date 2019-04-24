package tpiskorski.machinator.core.server

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.core.vm.VirtualMachine

class AddLocalServerServiceTest extends Specification {

    def serverService = Mock(ServerService)

    @Subject addLocalServerService = new AddLocalServerService(
            serverService: serverService
    )

    def 'should create action that does server and vms update'() {
        given:
        def server = Mock(Server)
        def vms = [Mock(VirtualMachine)]
        def action = addLocalServerService.addServerAndVmsAction(server, vms)

        when:
        action.perform()

        then:
        1 * serverService.add(server)
        1 * serverService.upsert(server, vms)
    }
}
