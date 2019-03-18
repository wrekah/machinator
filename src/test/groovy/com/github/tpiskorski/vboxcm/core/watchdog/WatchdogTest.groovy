package com.github.tpiskorski.vboxcm.core.watchdog

import spock.lang.Specification

class WatchdogTest extends Specification {

    def 'should properly compare equal watchdogs'() {
        given:
        def watchdog1 = new Watchdog(
                server: 'server1',
                vmName: 'vm1',
                watchdogServer: 'server2'
        )

        def watchdog2 = new Watchdog(
                server: 'server1',
                vmName: 'vm1',
                watchdogServer: 'server2'
        )

        expect:
        watchdog1 == watchdog2
        watchdog2 == watchdog1

        and:
        watchdog1.hashCode() == watchdog2.hashCode()
    }

    def 'should properly compare not equal watchdogs by backup server'() {
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

        expect:
        watchdog1 != watchdog2
        watchdog2 != watchdog1
    }

    def 'should properly compare not equal watchdogs by server'() {
        given:
        def watchdog1 = new Watchdog(
                server: 'server1',
                vmName: 'vm1',
                watchdogServer: 'server2'
        )

        def watchdog2 = new Watchdog(
                server: 'server2',
                vmName: 'vm1',
                watchdogServer: 'server2'
        )

        expect:
        watchdog1 != watchdog2
        watchdog2 != watchdog1
    }

    def 'should properly compare not equal watchdogs by vm'() {
        given:
        def watchdog1 = new Watchdog(
                server: 'server1',
                vmName: 'vm1',
                watchdogServer: 'server2'
        )

        def watchdog2 = new Watchdog(
                server: 'server1',
                vmName: 'vm2',
                watchdogServer: 'server2'
        )

        expect:
        watchdog1 != watchdog2
        watchdog2 != watchdog1
    }

    def 'should properly compare not watchdog'() {
        given:
        def something = new Object()
        def watchdog = new Watchdog(
                server: 'server1',
                vmName: 'vm1',
                watchdogServer: 'server2'
        )

        expect:
        something != watchdog

        and:
        watchdog != something
    }
}


