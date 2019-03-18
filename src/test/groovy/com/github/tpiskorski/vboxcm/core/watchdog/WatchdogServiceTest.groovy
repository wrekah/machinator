package com.github.tpiskorski.vboxcm.core.watchdog

import spock.lang.Specification
import spock.lang.Subject

class WatchdogServiceTest extends Specification {

    def watchdogRepository = Mock(WatchdogRepository)

    @Subject service = new WatchdogService(watchdogRepository)

    def 'should add watchdog'() {
        given:
        def watchdog = Mock(Watchdog)

        when:
        service.add(watchdog)

        then:
        1 * watchdogRepository.add(watchdog)
    }

    def 'should remove watchdog'() {
        given:
        def watchdog = Mock(Watchdog)

        when:
        service.remove(watchdog)

        then:
        1 * watchdogRepository.remove(watchdog)
    }

    def 'should get all watchdogs'() {
        when:
        service.getWatchdogs()

        then:
        1 * watchdogRepository.getWatchdogs()
    }
}
