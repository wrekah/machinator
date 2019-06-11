package tpiskorski.machinator.lifecycle.state.manager

import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.lifecycle.state.serialize.io.ObjectPersister
import tpiskorski.machinator.lifecycle.state.serialize.io.ObjectRestorer
import tpiskorski.machinator.lifecycle.state.serialize.model.SerializableVirtualMachine
import tpiskorski.machinator.model.server.Credentials
import tpiskorski.machinator.model.server.Server
import tpiskorski.machinator.model.vm.VirtualMachine
import tpiskorski.machinator.model.vm.VirtualMachineService

class VirtualMachineStateManagerTest extends Specification {

    def virtualMachineService = Mock(VirtualMachineService)

    def objectPersister = Mock(ObjectPersister)
    def objectRestorer = Mock(ObjectRestorer)

    @Subject persister = new VirtualMachineStateManager(virtualMachineService)

    def setup() {
        persister.objectPersister = objectPersister
        persister.objectRestorer = objectRestorer
    }

    def 'should persist vms state'() {
        given:
        def vms = createVms()

        when:
        persister.persist()

        then:
        1 * virtualMachineService.getVms() >> vms
        1 * objectPersister.persist(_, _)
    }

    def 'should restore vms state'() {
        given:
        def vms = createSerializableVms()

        when:
        persister.restore()

        then:
        1 * objectRestorer.restore(_) >> vms
        3 * virtualMachineService.add(_)
    }

    def 'should not restore anything if io exception is thrown'() {
        when:
        persister.restore()

        then:
        1 * objectRestorer.restore(_) >> { throw new IOException() }
        0 * _
    }

    def 'should not restore anything if class not found exception is thrown'() {
        when:
        persister.restore()

        then:
        1 * objectRestorer.restore(_) >> { throw new ClassNotFoundException() }
        0 * _
    }

    def createVms() {
        def server1 = new Server(new Credentials("user", "pw"), 'some', '123')
        def server2 = new Server(Credentials.none(), 'other', '321')

        [
                new VirtualMachine(server1, 'id1'),
                new VirtualMachine(server2, 'id1'),
                new VirtualMachine(server1, 'id2')
        ] as ObservableList
    }

    def createSerializableVms() {
        createVms().collect { new SerializableVirtualMachine(it) }
    }
}
