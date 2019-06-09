package tpiskorski.machinator.lifecycle.quartz

import groovy.time.TimeCategory
import org.quartz.Scheduler
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import tpiskorski.machinator.lifecycle.state.manager.PersistenceType

class StatePersistSchedulerTest extends Specification {

    def scheduler = Mock(Scheduler)

    @Subject statePersistScheduler = new StatePersistScheduler(scheduler)

    @Unroll
    def 'should schedule state persist'() {
        when:
        statePersistScheduler.schedulePersistence(persistenceType)

        then:
        1 * scheduler.scheduleJob(_, _) >> {
            def jobDetail = it[0]
            assert jobDetail.jobDataMap[StatePersistJob.PERSISTENCE_TYPE_KEY] == persistenceType
            def trigger = it[1]
            use(TimeCategory) {
                def duration = new Date() - trigger.startTime
                assert duration.seconds <= 5
            }
        }

        where:
        persistenceType << [
                PersistenceType.SERVER,
                PersistenceType.BACKUP_DEFINITION,
                PersistenceType.WATCHDOG
        ]
    }
}
