package tpiskorski.machinator.model.watchdog

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.flow.quartz.watchdog.WatchdogScheduler
import tpiskorski.machinator.lifecycle.quartz.PersistScheduler
import tpiskorski.machinator.lifecycle.state.manager.PersistenceType

class WatchdogServiceTest extends Specification {

    def watchdogRepository = Mock(WatchdogRepository)
    def watchdogScheduler = Mock(WatchdogScheduler)
    def persistScheduler = Mock(PersistScheduler)

    @Subject service = new WatchdogService(watchdogRepository, watchdogScheduler, persistScheduler)

    def 'should add watchdog and scheduler persistence'() {
        given:
        def watchdog = Mock(Watchdog)

        when:
        service.add(watchdog)

        then:
        1 * watchdogRepository.add(watchdog)
        1 * persistScheduler.schedulePersistence(PersistenceType.WATCHDOG)
    }

    def 'should add watchdog without scheduling persistence'() {
        given:
        def watchdog = Mock(Watchdog)

        when:
        service.put(watchdog)

        then:
        1 * watchdogRepository.add(watchdog)
        0 * persistScheduler.schedulePersistence(PersistenceType.WATCHDOG)
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

    def 'should check if contains watchdog'() {
        given:
        def watchdog = Mock(Watchdog)

        when:
        service.contains(watchdog)

        then:
        1 * watchdogRepository.contains(watchdog)
    }
}
