package tpiskorski.vboxcm.shutdown.state.restore

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.vboxcm.shutdown.state.persist.Persister

class AppStateRestorerTest extends Specification {

    def persisters = [Mock(Persister), Mock(Persister), Mock(Persister)]

    @Subject appStateRestorer = new AppStateRestorer(persisters)

    def 'should restore app state'() {
        when:
        appStateRestorer.afterPropertiesSet()

        then:
        3 * _.restore()
    }
}
