package tpiskorski.vboxcm.shutdown.state.persist

import spock.lang.Specification
import spock.lang.Subject

class DefaultAppStatePersisterTest extends Specification {

    def persisters = [Mock(Persister), Mock(Persister), Mock(Persister)]

    @Subject appPersister = new DefaultAppStatePersister(persisters)

    def 'should persist app state'() {
        when:
        appPersister.persist()

        then:
        3 * _.persist()
    }
}
