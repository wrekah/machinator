package tpiskorski.vboxcm.core.watchdog

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.vm.VirtualMachine

class WatchdogServiceModuleTest extends Specification {

    def watchdogRepository = new WatchdogRepository()

    @Subject service = new WatchdogService(watchdogRepository)

    def server1 = new Server('some', '123')
    def server2 = new Server('some', '321')
    def server3 = new Server('other', '123')

    def vm1 = new VirtualMachine(server1, 'id1')
    def vm2 = new VirtualMachine(server2, 'id1')

    def 'should get no watchdogs if nothing was added'() {
        expect:
        service.getWatchdogs().empty
    }

    def 'should get watchdogs that were added'() {
        given:
        def watchdog1 = new Watchdog(vm1, server2)
        def watchdog2 = new Watchdog(vm1, server3)
        def watchdog3 = new Watchdog(vm2, server1)

        when:
        service.add(watchdog1)
        service.add(watchdog2)
        service.add(watchdog3)

        then:
        service.getWatchdogs() == [watchdog1, watchdog2, watchdog3]
    }

    def 'should properly remove watchdogs'() {
        given:
        def watchdog1 = new Watchdog(vm1, server2)
        def watchdog2 = new Watchdog(vm1, server3)
        def watchdog3 = new Watchdog(vm2, server1)

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
        def watchdog1 = new Watchdog(vm1, server2)
        def watchdog2 = new Watchdog(vm1, server3)

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
