package tpiskorski.machinator.lifecycle.state

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.lifecycle.state.manager.BackupStateManager
import tpiskorski.machinator.lifecycle.state.manager.ServerStateManager
import tpiskorski.machinator.lifecycle.state.manager.VirtualMachineStateManager
import tpiskorski.machinator.lifecycle.state.manager.WatchdogStateManager

class AppStateRestorerTest extends Specification {

    def serverStateManager = Mock(ServerStateManager)
    def virtualMachineStateManager = Mock(VirtualMachineStateManager)
    def backupStateManager = Mock(BackupStateManager)
    def watchdogStateManager = Mock(WatchdogStateManager)

    @Subject appStateRestorer = new AppStateRestorer(
            serverStateManager, virtualMachineStateManager, backupStateManager, watchdogStateManager
    )

    def 'should restore app state'() {
        when:
        appStateRestorer.afterPropertiesSet()

        then:
        1 * serverStateManager.restore()
        1 * virtualMachineStateManager.restore()
        1 * backupStateManager.restore()
        1 * watchdogStateManager.restore()
    }
}
