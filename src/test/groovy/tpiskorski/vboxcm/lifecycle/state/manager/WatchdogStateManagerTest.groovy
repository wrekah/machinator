package tpiskorski.vboxcm.lifecycle.state.manager

import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.vm.VirtualMachine
import tpiskorski.vboxcm.core.watchdog.Watchdog
import tpiskorski.vboxcm.core.watchdog.WatchdogService
import tpiskorski.vboxcm.lifecycle.state.serialize.io.ObjectPersister
import tpiskorski.vboxcm.lifecycle.state.serialize.io.ObjectRestorer
import tpiskorski.vboxcm.lifecycle.state.serialize.model.SerializableWatchdog

class WatchdogStateManagerTest extends Specification {

    def watchdogService = Mock(WatchdogService)

    def objectPersister = Mock(ObjectPersister)
    def objectRestorer = Mock(ObjectRestorer)

    @Subject persister = new WatchdogStateManager(watchdogService)

    def setup() {
        persister.objectPersister = objectPersister
        persister.objectRestorer = objectRestorer
    }

    def 'should persist watchdogs state'() {
        given:
        def watchdogs = createWatchdogs()

        when:
        persister.persist()

        then:
        1 * watchdogService.getWatchdogs() >> watchdogs
        1 * objectPersister.persist(_, _)
    }

    def 'should restore watchdogs state'() {
        given:
        def watchdogs = createSerializableWatchdogs()

        when:
        persister.restore()

        then:
        1 * objectRestorer.restore(_, _) >> watchdogs
        3 * watchdogService.add(_)
    }

    def createWatchdogs() {
        def server1 = new Server('some', '123')
        def server2 = new Server('some', '321')
        def server3 = new Server('other', '123')

        def vm1 = new VirtualMachine(server1, 'id1')
        def vm2 = new VirtualMachine(server2, 'id1')

        [
                new Watchdog(vm1, server3),
                new Watchdog(vm2, server3),
                new Watchdog(vm2, server1)
        ] as ObservableList
    }

    def createSerializableWatchdogs() {
        createWatchdogs().collect { new SerializableWatchdog(it) }
    }
}
