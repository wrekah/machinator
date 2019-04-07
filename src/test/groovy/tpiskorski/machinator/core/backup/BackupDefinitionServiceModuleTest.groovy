package tpiskorski.machinator.core.backup

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.core.server.Server
import tpiskorski.machinator.core.vm.VirtualMachine

class BackupDefinitionServiceModuleTest extends Specification {

    def backupRepository = new BackupDefinitionRepository()

    @Subject service = new BackupDefinitionService(backupRepository, backupService)

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

    def 'should do the update'() {
        given:
        def oldBackup = new BackupDefinition(server1, vm1)
        def newBackup = new BackupDefinition(server1, vm1)

        and:
        oldBackup.fileLimit = 1
        newBackup.fileLimit = 10

        and:
        service.add(oldBackup)

        when:
        service.update(newBackup)

        then:
        service.getBackups() == [newBackup]
        service.getBackups().first().fileLimit == 10
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
}
