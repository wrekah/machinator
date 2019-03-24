package com.github.tpiskorski.vboxcm.core.watchdog

import com.github.tpiskorski.vboxcm.core.server.Server
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine
import spock.lang.Specification
import spock.lang.Subject

class WatchdogRepositoryTest extends Specification {

    @Subject repository = new WatchdogRepository()

    def server1 = new Server('some', '123')
    def server2 = new Server('some', '321')
    def server3 = new Server('other', '123')

    def vm1 = new VirtualMachine(server1, 'id1')
    def vm2 = new VirtualMachine(server2, 'id1')

    def 'should get no watchdogs if nothing was added'() {
        expect:
        repository.getWatchdogs().empty
    }

    def 'should add watchdogs'() {
        given:
        def watchdog1 = new Watchdog(
                virtualMachine: vm1,
                watchdogServer: server3
        )

        def watchdog2 = new Watchdog(
                virtualMachine: vm2,
                watchdogServer: server3
        )
        def watchdog3 = new Watchdog(
                virtualMachine: vm2,
                watchdogServer: server1
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
                virtualMachine: vm1,
                watchdogServer: server3
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
                virtualMachine: vm1,
                watchdogServer: server2
        )

        def watchdog2 = new Watchdog(
                virtualMachine: vm1,
                watchdogServer: server3
        )
        def watchdog3 = new Watchdog(
                virtualMachine: vm2,
                watchdogServer: server1
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
