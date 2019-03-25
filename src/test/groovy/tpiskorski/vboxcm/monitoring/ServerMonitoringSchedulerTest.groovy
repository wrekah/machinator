package tpiskorski.vboxcm.monitoring

import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.server.ServerService
import tpiskorski.vboxcm.core.server.ServerType
import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject

class ServerMonitoringSchedulerTest extends Specification {

    def serverService = Mock(ServerService)
    def serverMonitoringDaemon = Mock(ServerMonitoringDaemon)

    @Subject scheduler = new ServerMonitoringScheduler(serverService, serverMonitoringDaemon)

    def 'should schedule scan'() {
        given:
        def server = Mock(Server)

        when:
        scheduler.scheduleScan(server)

        then:
        1 * serverMonitoringDaemon.scheduleScan(server)
    }

    def 'should do regular schedule on all servers'() {
        given:
        def local = Mock(Server) {
            getServerType() >> ServerType.LOCAL
        }
        def remote = Mock(Server) {
            getServerType() >> ServerType.REMOTE
        }
        def servers = [local, remote, remote] as ObservableList

        when:
        scheduler.scheduleRegularScans()

        then:
        1 * serverService.getServers() >> servers
        1 * serverMonitoringDaemon.scheduleScan(local)
    }
}
