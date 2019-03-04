package com.github.tpiskorski.vboxcm.core.job

import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class JobServiceTest extends Specification {

    def jobRepository = new JobRepository()

    @Subject service = new JobService(jobRepository)

    def 'should get no jobs if nothing was added'() {
        expect:
        service.getJobs().empty
    }

    def 'should get jobs that were added'() {
        given:
        def job1 = new Job('job1', LocalDateTime.parse('2019-03-04T03:06'))
        def job2 = new Job('job2', LocalDateTime.parse('2019-03-04T03:06'))

        when:
        service.add(job1)
        service.add(job2)

        then:
        service.getJobs() == [job1, job2]
    }

    def 'should properly remove jobs'() {
        given:
        def job1 = new Job('job1', LocalDateTime.parse('2019-03-04T03:06'))
        def job2 = new Job('job2', LocalDateTime.parse('2019-03-04T03:06'))

        when:
        service.add(job1)
        service.add(job2)

        then:
        service.getJobs() == [job1, job2]

        when:
        service.remove(job1)

        then:
        service.getJobs() == [job2]

        when:
        service.remove(job2)

        then:
        service.getJobs().empty
    }

    def 'should not remove the job that is not present'() {
        given:
        def job1 = new Job('job1', LocalDateTime.parse('2019-03-04T03:06'))
        def job2 = new Job('job2', LocalDateTime.parse('2019-03-04T03:06'))

        when:
        service.add(job1)

        then:
        service.getJobs() == [job1]

        when:
        service.remove(job2)

        then:
        service.getJobs() == [job1]
    }

    def 'should stop job'() {
        given:
        def job1 = new Job('job1', LocalDateTime.parse('2019-03-04T03:06'))
        def job2 = new Job('job2', LocalDateTime.parse('2019-03-04T03:06'))

        when:
        service.add(job1)
        service.add(job2)

        and:
        service.stopJob(job1)

        then:
        job1.isStopped()
        !job2.isStopped()
    }

    def 'should stop all jobs'() {
        given:
        def job1 = new Job('job1', LocalDateTime.parse('2019-03-04T03:06'))
        def job2 = new Job('job2', LocalDateTime.parse('2019-03-04T03:06'))

        when:
        service.add(job1)
        service.add(job2)

        and:
        service.stopAllJobs()

        then:
        job1.isStopped()
        job2.isStopped()
    }
}
