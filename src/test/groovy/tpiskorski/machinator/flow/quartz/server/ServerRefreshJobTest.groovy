package tpiskorski.machinator.flow.quartz.server

import javafx.collections.ObservableList
import org.quartz.JobExecutionContext
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.flow.quartz.service.VmLister
import tpiskorski.machinator.model.server.Server
import tpiskorski.machinator.model.server.ServerService
import tpiskorski.machinator.model.server.ServerType
import tpiskorski.machinator.ui.core.PlatformThreadUpdater

class ServerRefreshJobTest extends Specification {

    def serverService = Mock(ServerService)
    def vmLister = Mock(VmLister)
    def platformThreadUpdater = Mock(PlatformThreadUpdater)

    @Subject job = new ServerRefreshJob(
            serverService, vmLister, platformThreadUpdater
    )

    def 'should not do server refresh job if there are no servers'() {
        given:
        def jobExecutionContext = Mock(JobExecutionContext)

        when:
        job.executeInternal(jobExecutionContext)

        then:
        1 * serverService.getServers() >> ([] as ObservableList)
        0 * vmLister.list(_)
        0 * platformThreadUpdater.runLater(_)
    }

    def 'should refresh each server'() {
        given:
        def jobExecutionContext = Mock(JobExecutionContext)
        def server1 = Mock(Server) {
            getServerType() >> ServerType.LOCAL
        }
        def server2 = Mock(Server) {
            getServerType() >> ServerType.REMOTE
        }

        when:
        job.executeInternal(jobExecutionContext)

        then:
        1 * serverService.getServers() >> ([server1, server2] as ObservableList)
        2 * vmLister.list(_)
        2 * platformThreadUpdater.runLater(_)
    }
}
