package com.github.tpiskorski.vboxcm.core.job

import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class JobTest extends Specification {

    @Subject repository = new JobRepository()

    def 'should add job'() {
        given:
        def job = new Job('job', LocalDateTime.parse('2019-03-15T02:12'))

        when:
        repository.add(job)

        then:
        repository.getJobsList() == [job]
    }

    def 'should get no jobs'() {
        expect:
        repository.getJobsList().empty
    }

    def 'should get jobs that were added'() {
        given:
        def job1 = new Job('job1', LocalDateTime.parse('2019-03-15T02:12'))
        def job2 = new Job('job2', LocalDateTime.parse('2019-03-15T02:12'))

        and:
        repository.add(job1)
        repository.add(job2)

        expect:
        repository.getJobsList() == [job1, job2]
    }

    def 'should remove job'() {
        given:
        def job = new Job('job', LocalDateTime.parse('2019-03-15T02:12'))

        when:
        repository.add(job)
        repository.remove(job)

        then:
        repository.getJobsList().empty
    }

    def 'should add and remove jobs'() {
        given:
        def job1 = new Job('job1', LocalDateTime.parse('2019-03-15T02:12'))
        def job2 = new Job('job1', LocalDateTime.parse('2020-03-15T02:13'))
        def job3 = new Job('job2', LocalDateTime.parse('2019-03-15T02:12'))

        when:
        repository.add(job1)
        repository.add(job2)
        repository.add(job3)

        then:
        repository.getJobsList() == [job1, job2, job3]

        when:
        repository.remove(job1)

        then:
        repository.getJobsList() == [job2, job3]

        when:
        repository.remove(job2)

        then:
        repository.getJobsList() == [job3]

        when:
        repository.remove(job3)

        then:
        repository.getJobsList().empty
    }
}
