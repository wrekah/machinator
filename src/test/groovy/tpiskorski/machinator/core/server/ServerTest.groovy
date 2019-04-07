package tpiskorski.machinator.core.server

import spock.lang.Specification

class ServerTest extends Specification {

    def 'should create server with unknown state server by default'() {
        given:
        def address = 'some'
        def port = '123'
        def server = new Server(address, port)

        expect:
        server.address == address
        server.port == port
        server.serverState == ServerState.UNKNOWN
    }

    def 'should properly compare not equal servers by port'() {
        given:
        def server1 = new Server('some', '123')
        def server2 = new Server('some', '321')

        expect:
        server1 != server2

        and:
        server2 != server1
    }

    def 'should properly compare not equal servers by address'() {
        given:
        def server1 = new Server('some1', '123')
        def server2 = new Server('some2', '123')

        expect:
        server1 != server2

        and:
        server2 != server1
    }

    def 'should properly equal servers'() {
        given:
        def server1 = new Server('some', '123')
        def server2 = new Server('some', '123')

        expect:
        server1 == server2
        server2 == server1

        and:
        server1.hashCode() == server2.hashCode()
    }

    def 'should properly compare not server'() {
        given:
        def something = new Object()
        def server = new Server('some', '123')

        expect:
        something != server

        and:
        server != something
    }

    def 'should create localhost server'() {
        given:
        def server = new Server('Local Machine', '')

        expect:
        server.address == 'Local Machine'
        server.serverType == ServerType.LOCAL
    }
}
