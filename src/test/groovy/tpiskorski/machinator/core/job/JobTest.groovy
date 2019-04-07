package tpiskorski.machinator.core.job

import spock.lang.Specification

class JobTest extends Specification {

    def 'should properly compare  not equal jobs'() {
        given:
        def job1 = new Job('job1')
        def job2 = new Job('job2')
        def job3 = new Job('job3')

        expect:
        job1 != job2
        job2 != job3
        job1 != job3

        and:
        job2 != job1
        job3 != job2
        job3 != job1
    }

    def 'should properly compare equal jobs'() {
        given:
        def job1 = new Job('job1')
        def job2 = new Job('job1')

        expect:
        job1 == job2

        and:
        job2 == job1

        and:
        job1.hashCode() == job2.hashCode()
    }

    def 'should properly compare not job'() {
        given:
        def something = new Object()
        def job = new Job('job')

        expect:
        something != job

        and:
        job != something
    }

    def 'should create initialized job by default'() {
        expect:
        (new Job('job')).status == JobStatus.INITIALIZED
    }
}
