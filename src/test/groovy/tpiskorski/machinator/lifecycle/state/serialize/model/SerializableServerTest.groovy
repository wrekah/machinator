package tpiskorski.machinator.lifecycle.state.serialize.model

import spock.lang.Specification
import tpiskorski.machinator.model.server.Credentials
import tpiskorski.machinator.model.server.Server

class SerializableServerTest extends Specification {

    def 'should create serializable server from server and convert it back'() {
        given:
        def address = 'localhost'
        def port = '8889'
        def credentials = new Credentials('user', 'password')
        def server = new Server(credentials, address, port)

        and:
        def serializableServer = new SerializableServer(server)

        and:
        def convertedBackServer = serializableServer.toServer()

        expect:
        convertedBackServer.address == address
        convertedBackServer.port == port
        convertedBackServer.credentials == credentials
        convertedBackServer == server
    }
}
