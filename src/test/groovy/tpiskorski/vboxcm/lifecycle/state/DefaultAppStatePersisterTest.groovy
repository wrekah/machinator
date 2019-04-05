package tpiskorski.vboxcm.lifecycle.state

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.vboxcm.lifecycle.state.manager.StateManager

class DefaultAppStatePersisterTest extends Specification {

    def persisters = [Mock(StateManager), Mock(StateManager), Mock(StateManager)]

    @Subject appPersister = new DefaultAppStatePersister(persisters)

    def 'should persist app state'() {
        when:
        appPersister.persist()

        then:
        3 * _.persist()
    }
}
