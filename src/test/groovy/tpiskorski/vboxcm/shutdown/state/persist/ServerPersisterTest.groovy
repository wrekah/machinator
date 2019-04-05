package tpiskorski.vboxcm.shutdown.state.persist

import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.server.ServerService

class ServerPersisterTest extends Specification {

    def serverService = Mock(ServerService)
    def objectPersister = Mock(ObjectPersister)

    @Subject persister = new ServerPersister(serverService)

    def setup() {
        persister.objectPersister = objectPersister
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

    def createServers() {
        [
                new Server('some', '123'),
                new Server('some', '321'),
                new Server('other', '123')
        ] as ObservableList
    }
}
