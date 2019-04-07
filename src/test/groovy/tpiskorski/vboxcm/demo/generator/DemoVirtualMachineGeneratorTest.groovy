package tpiskorski.vboxcm.demo.generator

import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.server.ServerService
import tpiskorski.vboxcm.core.vm.VirtualMachineService
import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject

class DemoVirtualMachineGeneratorTest extends Specification {

    def serverService = Mock(ServerService)
    def virtualMachineService = Mock(VirtualMachineService)

    @Subject generator = new DemoVirtualMachineGenerator(
            serverService, virtualMachineService
    )

    def 'should generate vms for each server'() {
        given:
        def servers = ([Mock(Server)] * 5) as ObservableList

        when:
        generator.afterPropertiesSet()

        then:
        1 * serverService.getServers() >> servers
        (5.._) * virtualMachineService.add(*_)
    }
}
