package com.github.tpiskorski.vboxcm.core.watchdog

import spock.lang.Specification
import spock.lang.Subject

class WatchdogRepositoryTest extends Specification {

    @Subject repository = new WatchdogRepository()

    def 'should get no watchdogs if nothing was added'() {
        expect:
        repository.getWatchdogs().empty
    }

    def 'should add watchdogs'() {
        given:
        def watchdog1 = new Watchdog(
                server: 'server1',
                vmName: 'vm1',
                watchdogServer: 'server2'
        )

        def watchdog2 = new Watchdog(
                server: 'server1',
                vmName: 'vm1',
                watchdogServer: 'server3'
        )
        def watchdog3 = new Watchdog(
                server: 'server2',
                vmName: 'vm2',
                watchdogServer: 'server1'
        )

        when:
        repository.add(watchdog1)
        repository.add(watchdog2)
        repository.add(watchdog3)

        then:
        repository.getWatchdogs() == [watchdog1, watchdog2, watchdog3]
    }

    def 'should remove watchdog'() {
        given:
        def watchdog = new Watchdog(
                server: 'server1',
                vmName: 'vm1',
                watchdogServer: 'server2'
        )

        when:
        repository.add(watchdog)
        repository.remove(watchdog)

        then:
        repository.getWatchdogs().empty
    }

    def 'should add and remove watchdogs'() {
        given:
        def watchdog1 = new Watchdog(
                server: 'server1',
                vmName: 'vm1',
                watchdogServer: 'server2'
        )

        def watchdog2 = new Watchdog(
                server: 'server1',
                vmName: 'vm1',
                watchdogServer: 'server3'
        )
        def watchdog3 = new Watchdog(
                server: 'server2',
                vmName: 'vm2',
                watchdogServer: 'server1'
        )

        when:
        repository.add(watchdog1)
        repository.add(watchdog2)
        repository.add(watchdog3)

        then:
        repository.getWatchdogs() == [watchdog1, watchdog2, watchdog3]

        when:
        repository.remove(watchdog1)

        then:
        repository.getWatchdogs() == [watchdog2, watchdog3]

        when:
        repository.remove(watchdog2)

        then:
        repository.getWatchdogs() == [watchdog3]

        when:
        repository.remove(watchdog3)

        then:
        repository.getWatchdogs().empty
    }
}
