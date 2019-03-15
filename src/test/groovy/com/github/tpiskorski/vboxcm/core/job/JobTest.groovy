package com.github.tpiskorski.vboxcm.core.job

import spock.lang.Specification

import java.time.LocalDateTime

class JobTest extends Specification {

    def 'should properly compare  not equal jobs'() {
        given:
        def job1 = new Job('job1', LocalDateTime.parse('2019-03-15T20:20'))
        def job2 = new Job('job2', LocalDateTime.parse('2019-03-15T20:20'))
        def job3 = new Job('jo1', LocalDateTime.parse('2012-03-15T20:20'))

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
        def job1 = new Job('job1', LocalDateTime.parse('2019-03-15T20:20'))
        def job2 = new Job('job1', LocalDateTime.parse('2019-03-15T20:20'))

        expect:
        job1 == job2

        and:
        job2 == job1
    }

    def 'should properly compare not job'() {
        given:
        def something = new Object()
        def job = new Job('job', LocalDateTime.parse('2019-03-15T20:20'))

        expect:
        something != job

        and:
        job != something
    }
}
