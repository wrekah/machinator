package tpiskorski.machinator.model.job

import spock.lang.Specification
import spock.lang.Subject

class JobServiceTest extends Specification {

    def jobRepository = Mock(JobRepository)

    @Subject service = new JobService(jobRepository)

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
