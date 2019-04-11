package tpiskorski.machinator.core.backup

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.core.server.Server
import tpiskorski.machinator.core.vm.VirtualMachine
import tpiskorski.machinator.quartz.backup.BackupScheduler

class BackupDefinitionServiceModuleTest extends Specification {

    def backupRepository = new BackupDefinitionRepository()
    def backupScheduler = Mock(BackupScheduler)

    @Subject service = new BackupDefinitionService(
            backupRepository, backupScheduler
    )

    def server1 = new Server('some', '123')
    def server2 = new Server('other', '321')

    def vm1 = new VirtualMachine(server1, 'id1')
    def vm2 = new VirtualMachine(server2, 'id1')

    def 'should get no backups'() {
        expect:
        service.getBackups().empty
    }

    def 'should get backups that were added'() {
        given:
        def backup1 = new BackupDefinition(server1, vm1)
        def backup2 = new BackupDefinition(server1, vm2)
        def backup3 = new BackupDefinition(server2, vm1)

        when:
        service.add(backup1)
        service.add(backup2)
        service.add(backup3)

        then:
        service.getBackups() == [backup1, backup2, backup3]
    }

    def 'should properly remove servers'() {
        given:
        def backup1 = new BackupDefinition(server1, vm1)
        def backup2 = new BackupDefinition(server1, vm2)
        def backup3 = new BackupDefinition(server2, vm1)

        when:
        service.add(backup1)
        service.add(backup2)
        service.add(backup3)

        then:
        service.getBackups() == [backup1, backup2, backup3]

        when:
        service.remove(backup1)

        then:
        service.getBackups() == [backup2, backup3]

        when:
        service.remove(backup2)

        then:
        service.getBackups() == [backup3]

        when:
        service.remove(backup3)

        then:
        service.getBackups().empty
    }

    def 'should not remove backup that is not present'() {
        given:
        def backup1 = new BackupDefinition(server1, vm1)
        def backup2 = new BackupDefinition(server1, vm2)

        when:
        service.add(backup1)

        then:
        service.getBackups() == [backup1]

        when:
        service.remove(backup2)

        then:
        service.getBackups() == [backup1]
    }

    def 'should activate and deactivate watchdog'() {
        given:
        def backup = new BackupDefinition(server1, vm1)

        expect:
        !backup.active

        when:
        service.activate(backup)

        then:
        backup.active

        when:
        service.deactivate(backup)

        then:
        !backup.active
    }

    def 'should add to scheduler when activating backup'() {
        given:
        def backup1 = new BackupDefinition(server1, vm1)

        when:
        service.activate(backup1)

        then:
        1 * backupScheduler.addTaskToScheduler(backup1)

        and:
        backup1.isActive()
    }

    def 'should remove from scheduler when deactivating backup'() {
        given:
        def backup1 = new BackupDefinition(server1, vm1)

        when:
        service.deactivate(backup1)

        then:
        1 * backupScheduler.removeTaskFromScheduler(backup1)

        and:
        !backup1.isActive()
    }
}
