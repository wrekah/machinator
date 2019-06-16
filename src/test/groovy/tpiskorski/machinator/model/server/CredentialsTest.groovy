package tpiskorski.machinator.model.server

import spock.lang.Specification

class CredentialsTest extends Specification {

    def 'should create credentials'() {
        given:
        def user = 'user'
        def password = 'password'

        def credentials = new Credentials(user, password)

        expect:
        credentials.user == user
        credentials.password == password
    }

    def 'should be equal'() {
        given:
        def user = 'user'
        def password = 'password'

        def first = new Credentials(user, password)
        def second = new Credentials(user, password)

        expect:
        first == second
        first.hashCode() == second.hashCode()
    }

    def 'should be not equal'() {
        given:
        def first = new Credentials('user', 'pw')
        def second = new Credentials('other user', 'pw')

        expect:
        first != second
    }

    def 'should get credentials when factory method used'() {
        expect:
        Credentials.none()
    }
}
