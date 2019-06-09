package tpiskorski.machinator.lifecycle.quartz

import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import tpiskorski.machinator.lifecycle.state.manager.PersistenceType
import tpiskorski.machinator.lifecycle.state.manager.StateManager

class StatePersistJobTest extends Specification {

    def stateManagers = PersistenceType.enumConstants.collect { e ->
        Mock(StateManager) {
            getPersistenceType() >> e
        }
    }

    @Subject job = new StatePersistJob(stateManagers)

    @Unroll
    def 'should do a job for given persistence type'() {
        given:
        def jobExecutionContext = Mock(JobExecutionContext) {
            getMergedJobDataMap() >> Mock(JobDataMap) {
                get(StatePersistJob.PERSISTENCE_TYPE_KEY) >> (persistenceType)
            }
        }

        and:
        def stateManager = stateManagers.find { it.getPersistenceType() == persistenceType }

        when:
        job.executeInternal(jobExecutionContext)

        then:
        1 * stateManager.persist()

        and:
        stateManager.getPersistenceType() == persistenceType

        where:
        persistenceType << [
                PersistenceType.SERVER,
                PersistenceType.BACKUP_DEFINITION,
                PersistenceType.WATCHDOG
        ]
    }
}
