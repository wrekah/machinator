package tpiskorski.machinator.model.server

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
        def server = new Server('local', '')

        expect:
        server.address == 'local'
        server.serverType == ServerType.LOCAL
    }

    def 'should create server with credentials'() {
        given:
        def credentials = new Credentials('user', 'pw')
        def server = new Server(credentials, 'some', '123')

        expect:
        server.credentials == credentials
        server.address == 'some'
        server.port == '123'
        server.serverState == ServerState.UNKNOWN
    }

    def 'should get simple address'() {
        given:
        def server1 = new Server('local', '')
        def server2 = new Server('some', '123')

        expect:
        server1.getSimpleAddress() == 'local'
        server2.getSimpleAddress() == 'some:123'
    }

    def 'should call simple address when invoking tostring'() {
        given:
        def server1 = new Server('local', '')
        def server2 = new Server('some', '123')

        expect:
        server1.toString() == 'local'
        server2.toString() == 'some:123'
    }
}
