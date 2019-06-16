package tpiskorski.machinator.lifecycle.state.manager

import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.lifecycle.state.serialize.io.ObjectPersister
import tpiskorski.machinator.lifecycle.state.serialize.io.ObjectRestorer
import tpiskorski.machinator.lifecycle.state.serialize.model.SerializableWatchdog
import tpiskorski.machinator.model.server.Credentials
import tpiskorski.machinator.model.server.Server
import tpiskorski.machinator.model.vm.VirtualMachine
import tpiskorski.machinator.model.watchdog.Watchdog
import tpiskorski.machinator.model.watchdog.WatchdogService

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
        1 * objectRestorer.restore(_) >> watchdogs
        3 * watchdogService.put(_)
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

    def 'should not persist anything if exception is thrown'() {
        given:
        def watchdogs = createWatchdogs()

        when:
        persister.persist()

        then:
        1 * watchdogService.getWatchdogs() >> watchdogs
        1 * objectPersister.persist(_, _)
    }

    def createWatchdogs() {
        def server1 = new Server(new Credentials("user", "pw"), 'some', '123')
        def server2 = new Server(new Credentials("user", "pw"), 'some', '321')
        def server3 = new Server(Credentials.none(), 'other', '123')

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
