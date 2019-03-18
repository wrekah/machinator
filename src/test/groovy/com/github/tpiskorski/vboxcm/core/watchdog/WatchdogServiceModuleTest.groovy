package com.github.tpiskorski.vboxcm.core.watchdog

import spock.lang.Specification
import spock.lang.Subject

class WatchdogServiceModuleTest extends Specification {

    def watchdogRepository = new WatchdogRepository()

    @Subject service = new WatchdogService(watchdogRepository)

    def 'should get no watchdogs if nothing was added'() {
        expect:
        service.getWatchdogs().empty
    }

    def 'should get watchdogs that were added'() {
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
        service.add(watchdog1)
        service.add(watchdog2)
        service.add(watchdog3)

        then:
        service.getWatchdogs() == [watchdog1, watchdog2, watchdog3]
    }

    def 'should properly remove watchdogs'() {
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
        service.add(watchdog1)
        service.add(watchdog2)
        service.add(watchdog3)

        then:
        service.getWatchdogs() == [watchdog1, watchdog2, watchdog3]

        when:
        service.remove(watchdog1)

        then:
        service.getWatchdogs() == [watchdog2, watchdog3]

        when:
        service.remove(watchdog2)

        then:
        service.getWatchdogs() == [watchdog3]

        when:
        service.remove(watchdog3)

        then:
        service.getWatchdogs().empty
    }

    def 'should not remove the vm that is not present'() {
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

        when:
        service.add(watchdog1)

        then:
        service.getWatchdogs() == [watchdog1]

        when:
        service.remove(watchdog2)

        then:
        service.getWatchdogs() == [watchdog1]
    }
}
