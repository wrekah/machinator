package com.github.tpiskorski.vboxcm.core.server

import spock.lang.Specification
import spock.lang.Subject

class ServerRepositoryTest extends Specification {

    @Subject repository = new ServerRepository()

    def 'should add server'() {
        given:
        def server1 = new Server('localhost:2000')
        def server2 = new Server('localhost:1000')

        when:
        repository.add(server1)
        repository.add(server2)

        then:
        repository.getServersList() == [server1, server2]
    }

    def 'should remove server'() {
        given:
        def server1 = new Server('localhost:8888')

        when:
        repository.add(server1)
        repository.remove(server1)

        then:
        repository.getServersList().empty
    }

    def 'should add and remove servers'() {
        given:
        def server1 = new Server('localhost:8888')
        def server2 = new Server('localhost:8889')

        when:
        repository.add(server1)
        repository.add(server2)

        then:
        repository.getServersList() == [server1, server2]

        when:
        repository.remove(server1)

        then:
        repository.getServersList() == [server2]

        when:
        repository.remove(server2)

        then:
        repository.getServersList().empty
    }

    def 'should not find any elements if not present'() {
        given:
        def server1 = new Server('localhost:8888')
        def server2 = new Server('localhost:8889')

        when:
        repository.add(server1)

        then:
        !repository.contains(server2)
        repository.contains(server1)

        when:
        repository.remove(server1)

        then:
        !repository.contains(server2)
        !repository.contains(server1)
    }
}
