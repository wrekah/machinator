package tpiskorski.vboxcm.shutdown.state.persist

import spock.lang.Specification
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.shutdown.state.persist.SerializableServer

class SerializableServerTest extends Specification {

    def 'should create serializable server from server and convert it back'() {
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
