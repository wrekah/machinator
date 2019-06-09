package tpiskorski.machinator.lifecycle.quartz

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import tpiskorski.machinator.lifecycle.state.manager.PersistenceType

class NoopPersistSchedulerTest extends Specification {

    @Subject scheduler = new NoopPersistScheduler()

    @Unroll
    def 'should do nothing'() {
        when:
        scheduler.schedulePersistence(persistenceType)

        then:
        0 * _

        where:
        persistenceType << [
                PersistenceType.SERVER,
                PersistenceType.BACKUP_DEFINITION,
                PersistenceType.WATCHDOG
        ]
    }
}
