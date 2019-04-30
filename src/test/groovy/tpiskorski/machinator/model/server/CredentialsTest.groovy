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
}
