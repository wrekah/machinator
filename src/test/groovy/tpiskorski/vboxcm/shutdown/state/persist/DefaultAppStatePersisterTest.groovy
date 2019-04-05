package tpiskorski.vboxcm.shutdown.state.persist

import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.server.ServerService

class DefaultAppStatePersisterTest extends Specification {

    def serverService = Mock(ServerService)
    def objectPersister = Mock(ObjectPersister)

    @Subject persister = new DefaultAppStatePersister(serverService)

    def setup() {
        persister.objectPersister = objectPersister
    }

    def 'should persist app state'() {
        when:
        persister.persist()

        then:
        1 * serverService.getServers() >> ([Mock(Server)] as ObservableList)
        1 * objectPersister.persist(_, _)
    }
}
