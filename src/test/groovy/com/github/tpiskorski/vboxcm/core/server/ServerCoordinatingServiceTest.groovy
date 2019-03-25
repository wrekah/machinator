package com.github.tpiskorski.vboxcm.core.server

import com.github.tpiskorski.vboxcm.monitoring.ServerMonitoringScheduler
import spock.lang.Specification
import spock.lang.Subject

class ServerCoordinatingServiceTest extends Specification {

    def serverService = Mock(ServerService)
    def serverMonitoringScheduler = Mock(ServerMonitoringScheduler)

    @Subject service = new ServerCoordinatingService(serverService, serverMonitoringScheduler)

    def 'should add and schedule server'() {
        given:
        def server = Mock(Server)

        when:
        service.add(server)

        then:
        1 * serverService.add(server)
        1 * serverMonitoringScheduler.scheduleScan(server)
    }
}
