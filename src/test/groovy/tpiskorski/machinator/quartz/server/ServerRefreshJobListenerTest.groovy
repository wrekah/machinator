package tpiskorski.machinator.quartz.server

import org.quartz.JobDetail
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.core.job.JobService

class ServerRefreshJobListenerTest extends Specification {

    def jobService = Mock(JobService)

    @Subject listener = new ServerRefreshJobListener(jobService)

    def 'should only listen to server refresh job'() {
        given:
        def jobExecutionContext = Mock(JobExecutionContext) {
            getJobDetail() >> Mock(JobDetail) {
                getJobClass() >> ServerRefreshJobListenerTest.class
            }
        }
        def jobExecutionException = Mock(JobExecutionException)

        when:
        listener.jobToBeExecuted(jobExecutionContext)
        listener.jobExecutionVetoed(jobExecutionContext)
        listener.jobWasExecuted(jobExecutionContext, jobExecutionException)

        then:
        0 * jobService._
    }


}
