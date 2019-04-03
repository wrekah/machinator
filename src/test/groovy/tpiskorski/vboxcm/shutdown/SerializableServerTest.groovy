package tpiskorski.vboxcm.shutdown

import spock.lang.Specification
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.shutdown.state.SerializableServer

class SerializableServerTest extends Specification {

    def 'should create serializable server from server'() {
        given:
        def address = 'localhost'
        def port = '8889'
        def server = new Server(address, port)

        and:
        def serializableServer = new SerializableServer(server)

        expect:
        serializableServer.address == address
        serializableServer.port == port
    }

    def 'should convert serializable server back to server'(){
        given:
        def address = 'localhost'
        def port = '8889'
        def server = new Server(address, port)

        and:
        def serializableServer = new SerializableServer(server)

        and:
        def convertedBackServer = serializableServer.toServer()

        expect:
        convertedBackServer.address == address
        convertedBackServer.port == port
        convertedBackServer == server
    }
}
