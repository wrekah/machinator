package tpiskorski.machinator.flow.quartz.server

import org.quartz.*
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.model.job.JobService

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

    def 'should create job when server refresh job threw exception'() {

        given:
        def jobExecutionContext = Mock(JobExecutionContext) {
            getJobDetail() >> Mock(JobDetail) {
                getKey() >> JobKey.jobKey('someKey')
                getJobClass() >> ServerRefreshJob.class
            }
        }
        def jobExecutionException = Mock(JobExecutionException)

        when:
        listener.jobWasExecuted(jobExecutionContext, jobExecutionException)

        then:
        1 * jobExecutionException.getCause() >> Mock(SchedulerException) {
            getUnderlyingException() >> Mock(Throwable) {
                getMessage() >> 'message'
            }
        }
        1 * jobService.add(_)
    }
}
