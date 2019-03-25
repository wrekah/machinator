package tpiskorski.vboxcm.core.watchdog

import tpiskorski.vboxcm.core.server.Server
import spock.lang.Specification
import tpiskorski.vboxcm.core.vm.VirtualMachine

class WatchdogTest extends Specification {

    def server1 = new Server('some', '123')
    def server2 = new Server('some', '321')
    def server3 = new Server('other', '123')

    def vm1 = new VirtualMachine(server1, 'id1')
    def vm2 = new VirtualMachine(server2, 'id1')

    def 'should properly compare equal watchdogs'() {
        given:
        def watchdog1 = new Watchdog(
                virtualMachine: vm1,
                watchdogServer: server2
        )

        def watchdog2 = new Watchdog(
                virtualMachine: vm1,
                watchdogServer: server2
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
                virtualMachine: vm1,
                watchdogServer: server2
        )

        def watchdog2 = new Watchdog(
                virtualMachine: vm1,
                watchdogServer: server3
        )

        expect:
        watchdog1 != watchdog2
        watchdog2 != watchdog1
    }

    def 'should properly compare not equal watchdogs by server'() {
        given:
        def watchdog1 = new Watchdog(
                virtualMachine: vm1,
                watchdogServer: server2
        )

        def watchdog2 = new Watchdog(
                virtualMachine: vm2,
                watchdogServer: server2
        )

        expect:
        watchdog1 != watchdog2
        watchdog2 != watchdog1
    }

    def 'should properly compare not equal watchdogs by vm'() {
        given:
        def watchdog1 = new Watchdog(
                virtualMachine: vm1,
                watchdogServer: server2
        )

        def watchdog2 = new Watchdog(
                virtualMachine: vm2,
                watchdogServer: server2
        )

        expect:
        watchdog1 != watchdog2
        watchdog2 != watchdog1
    }

    def 'should properly compare not watchdog'() {
        given:
        def something = new Object()
        def watchdog = new Watchdog(
                virtualMachine: vm1,
                watchdogServer: server2
        )

        expect:
        something != watchdog

        and:
        watchdog != something
    }
}


