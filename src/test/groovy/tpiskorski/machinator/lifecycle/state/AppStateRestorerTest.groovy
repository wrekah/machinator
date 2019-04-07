package tpiskorski.machinator.lifecycle.state

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.lifecycle.state.manager.StateManager

class AppStateRestorerTest extends Specification {

    def persisters = [Mock(StateManager), Mock(StateManager), Mock(StateManager)]

    @Subject appStateRestorer = new AppStateRestorer(persisters)

    def 'should restore app state'() {
        when:
        appStateRestorer.afterPropertiesSet()

        then:
        3 * _.restore()
    }
}
