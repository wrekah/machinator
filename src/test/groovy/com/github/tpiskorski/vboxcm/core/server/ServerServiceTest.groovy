package com.github.tpiskorski.vboxcm.core.server

import spock.lang.Specification
import spock.lang.Subject

class ServerServiceTest extends Specification {

    def serverRepository = new ServerRepository()

    @Subject service = new ServerService(serverRepository)

    def 'should get no servers if nothing was added'() {
        expect:
        service.getServers().empty
    }

    def 'should get servers that were added'() {
        given:
        def server1 = new Server('some:address')
        def server2 = new Server('some:otheraddress')

        when:
        service.add(server1)
        service.add(server2)

        then:
        service.getServers() == [server1, server2]
    }

    def 'should properly remove servers'() {
        given:
        def server1 = new Server('some:address')
        def server2 = new Server('some:otheraddress')

        when:
        service.add(server1)
        service.add(server2)

        then:
        service.getServers() == [server1, server2]

        when:
        service.remove(server1)

        then:
        service.getServers() == [server2]

        when:
        service.remove(server2)

        then:
        service.getServers().empty
    }

    def 'should not remove the server that is not present'() {
        given:
        def server1 = new Server('some:address')
        def server2 = new Server('some:otheraddress')

        when:
        service.add(server1)

        then:
        service.getServers() == [server1]

        when:
        service.remove(server2)

        then:
        service.getServers() == [server1]
    }
}
