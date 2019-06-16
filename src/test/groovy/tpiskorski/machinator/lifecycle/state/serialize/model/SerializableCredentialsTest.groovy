package tpiskorski.machinator.lifecycle.state.serialize.model

import spock.lang.Specification
import tpiskorski.machinator.model.server.Credentials

class SerializableCredentialsTest extends Specification {

    def 'should create serializable credentials from credentials and convert it back'() {
        given:
        def user = 'user'
        def password = 'password'
        def credentials = new Credentials(user, password)

        and:
        def serializableCredentials = new SerializableCredentials(credentials)

        and:
        def convertedBackCredentials = serializableCredentials.toCredentials()

        expect:
        convertedBackCredentials.user == user
        convertedBackCredentials.password == password
    }
}
