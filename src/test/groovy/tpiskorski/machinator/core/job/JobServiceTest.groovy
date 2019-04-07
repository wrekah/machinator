package tpiskorski.machinator.core.job

import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject

class JobServiceTest extends Specification {

    def jobRepository = Mock(JobRepository)

    @Subject service = new JobService(jobRepository)

    def 'should stop job'() {
        given:
        def job = Mock(Job)

        when:
        service.stopJob(job)

        then:
        1 * job.setStatus(JobStatus.STOPPED)
    }

    def 'should stop all job'() {
        given:
        def jobs = [Mock(Job), Mock(Job), Mock(Job)] as ObservableList

        when:
        service.stopAllJobs()

        then:
        1 * jobRepository.getJobsList() >> jobs
        3 * _.setStatus(JobStatus.STOPPED)
    }

    def 'should get jobs'() {
        when:
        service.getJobs()

        then:
        1 * jobRepository.getJobsList()
    }

    def 'should add job'() {
        given:
        def job = Mock(Job)

        when:
        service.add(job)

        then:
        1 * jobRepository.add(job)
    }

    def 'should remove job'() {
        given:
        def job = Mock(Job)

        when:
        service.remove(job)

        then:
        1 * jobRepository.remove(job)
    }
}
