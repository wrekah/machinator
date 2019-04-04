package tpiskorski.vboxcm.shutdown.state

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.vboxcm.core.server.ServerService

class AppStateRestorerTest extends Specification {

    def serverService = Mock(ServerService)
    def objectRestorer = Mock(ObjectRestorer)

    @Subject restorer = new AppStateRestorer(serverService)

    def setup() {
        restorer.objectRestorer = objectRestorer
    }

    def 'should restore app state'() {
        when:
        restorer.afterPropertiesSet()

        then:
        1 * objectRestorer.restore(_, _) >> [Mock(SerializableServer)]
        1 * serverService.add(_)
    }
}
