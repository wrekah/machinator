package com.github.tpiskorski.vboxcm.core.server

import spock.lang.Specification

class ServerTest extends Specification {

    def 'should create server with unknown state server by default'() {
        given:
        def address = 'some:server'
        def server = new Server(address)

        expect:
        server.address == address
        server.serverState == ServerState.UNKNOWN
    }

    def 'should properly compare not equal servers'() {
        given:
        def server1 = new Server('some:server')
        def server2 = new Server('some:other_server')

        expect:
        server1 != server2
    }

    def 'should properly equal servers'() {
        given:
        def server1 = new Server('some:server')
        def server2 = new Server('some:server')

        expect:
        server1 == server2
        server1.hashCode() == server2.hashCode()
    }
}
