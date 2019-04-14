package tpiskorski.machinator.quartz.server

import org.quartz.JobDetail
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.JobKey
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.core.job.Job
import tpiskorski.machinator.core.job.JobService
import tpiskorski.machinator.core.job.JobStatus

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

    def 'should create job before server refresh'() {
        given:
        def jobExecutionContext = Mock(JobExecutionContext) {
            getJobDetail() >> Mock(JobDetail) {
                getKey() >> JobKey.jobKey('someKey')
                getJobClass() >> ServerRefreshJob.class
            }
        }

        when:
        listener.jobToBeExecuted(jobExecutionContext)

        then:
        1 * jobService.add(_) >> { assert (it[0].status == JobStatus.IN_PROGRESS) }
    }

    def 'should update job status when vetoed'() {
        given:
        def jobExecutionContext = Mock(JobExecutionContext) {
            getJobDetail() >> Mock(JobDetail) {
                getKey() >> JobKey.jobKey('someKey')
                getJobClass() >> ServerRefreshJob.class
            }
        }

        and:
        def job = Mock(Job)

        when:
        listener.jobExecutionVetoed(jobExecutionContext)

        then:
        1 * jobService.getLastServerRefreshJob() >> job
        1 * job.setStatus(JobStatus.CANCELLED)
    }

    def 'should update job status when executed without exceptions'() {
        given:
        def jobExecutionContext = Mock(JobExecutionContext) {
            getJobDetail() >> Mock(JobDetail) {
                getKey() >> JobKey.jobKey('someKey')
                getJobClass() >> ServerRefreshJob.class
            }
        }

        and:
        def job = Mock(Job)

        when:
        listener.jobWasExecuted(jobExecutionContext, null)

        then:
        1 * jobService.getLastServerRefreshJob() >> job
        1 * job.setStatus(JobStatus.COMPLETED)
    }

    def 'should update job status when failed to execute'() {

        given:
        def jobExecutionContext = Mock(JobExecutionContext) {
            getJobDetail() >> Mock(JobDetail) {
                getKey() >> JobKey.jobKey('someKey')
                getJobClass() >> ServerRefreshJob.class
            }
        }
        def jobExecutionException = Mock(JobExecutionException)

        and:
        def job = Mock(Job)

        when:
        listener.jobWasExecuted(jobExecutionContext, jobExecutionException)

        then:
        1 * jobService.getLastServerRefreshJob() >> job
        1 * job.setStatus(JobStatus.FAILED)
    }
}
