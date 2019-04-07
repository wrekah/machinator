package tpiskorski.machinator.demo.generator

import tpiskorski.machinator.core.server.Server
import tpiskorski.machinator.core.server.ServerService
import tpiskorski.machinator.core.vm.VirtualMachineService
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
