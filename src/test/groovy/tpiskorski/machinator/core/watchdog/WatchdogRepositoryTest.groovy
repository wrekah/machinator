package tpiskorski.machinator.core.watchdog

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.core.server.Server
import tpiskorski.machinator.core.vm.VirtualMachine

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
        def watchdog1 = new Watchdog(vm1, server3)
        def watchdog2 = new Watchdog(vm2, server3)
        def watchdog3 = new Watchdog(vm2, server1)

        when:
        repository.add(watchdog1)
        repository.add(watchdog2)
        repository.add(watchdog3)

        then:
        repository.getWatchdogs() == [watchdog1, watchdog2, watchdog3]
    }

    def 'should remove watchdog'() {
        given:
        def watchdog = new Watchdog(vm1, server3)

        when:
        repository.add(watchdog)
        repository.remove(watchdog)

        then:
        repository.getWatchdogs().empty
    }

    def 'should add and remove watchdogs'() {
        given:
        def watchdog1 = new Watchdog(vm1, server2)
        def watchdog2 = new Watchdog(vm1, server3)
        def watchdog3 = new Watchdog(vm2, server1)

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
