package tpiskorski.machinator.lifecycle.state.manager

import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.lifecycle.state.serialize.io.ObjectPersister
import tpiskorski.machinator.lifecycle.state.serialize.io.ObjectRestorer
import tpiskorski.machinator.lifecycle.state.serialize.model.SerializableServer
import tpiskorski.machinator.model.server.Credentials
import tpiskorski.machinator.model.server.Server
import tpiskorski.machinator.model.server.ServerService

class ServerStateManagerTest extends Specification {

    def serverService = Mock(ServerService)

    def objectPersister = Mock(ObjectPersister)
    def objectRestorer = Mock(ObjectRestorer)

    @Subject persister = new ServerStateManager(serverService)

    def setup() {
        persister.objectPersister = objectPersister
        persister.objectRestorer = objectRestorer
    }

    def 'should persist servers state'() {
        given:
        def servers = createServers()

        when:
        persister.persist()

        then:
        1 * serverService.getServers() >> servers
        1 * objectPersister.persist(_, _)
    }

    def 'should restore servers state'() {
        given:
        def servers = createSerializableServers()

        when:
        persister.restore()

        then:
        1 * objectRestorer.restore(_) >> servers
        3 * serverService.put(_)
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

    def createServers() {
        [
                new Server(new Credentials("user", "pw"),'some', '123'),
                new Server(new Credentials("user", "pw"),'some', '321'),
                new Server(Credentials.none(),'other', '123')
        ] as ObservableList
    }

    def createSerializableServers() {
        createServers().collect { new SerializableServer(it) }
    }
}
