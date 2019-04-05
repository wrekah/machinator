package tpiskorski.vboxcm.lifecycle.state

import spock.lang.Specification
import spock.lang.Subject

class NoopAppStatePersisterTest extends Specification {

    @Subject persister = new NoopAppStatePersister()

    def 'should do nothing'() {
        when:
        persister.persist()

        then:
        0 * _
    }
}
