package tpiskorski.vboxcm.lifecycle.state.serialize.model

import spock.lang.Specification
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.vm.VirtualMachine

class SerializableVirtualMachineTest extends Specification {
    def 'should create serializable virtual machine from virtual machine and covert it back'() {
        given:
        def server = new Server('some', '123')
        def id = 'id'
        def virtualMachine = new VirtualMachine(server, id)

        when:
        def serializableVirtualMachine = new SerializableVirtualMachine(virtualMachine)

        and:
        def convertedBackVirtualMachine = serializableVirtualMachine.toVirtualMachine()

        then:
        convertedBackVirtualMachine.server == server
        convertedBackVirtualMachine.id == id
        convertedBackVirtualMachine == virtualMachine
    }
}
