package tpiskorski.vboxcm.core.backup

import tpiskorski.vboxcm.core.server.Server
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.vboxcm.core.vm.VirtualMachine

class BackupServiceModuleTest extends Specification {

    def backupRepository = new BackupRepository()

    @Subject service = new BackupService(backupRepository)

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
        def backup1 = new Backup(server: server1, vm: vm1)
        def backup2 = new Backup(server: server1, vm: vm2)
        def backup3 = new Backup(server: server2, vm: vm1)

        when:
        service.add(backup1)
        service.add(backup2)
        service.add(backup3)

        then:
        service.getBackups() == [backup1, backup2, backup3]
    }

    def 'should properly remove servers'() {
        given:
        def backup1 = new Backup(server: server1, vm: vm1)
        def backup2 = new Backup(server: server1, vm: vm2)
        def backup3 = new Backup(server: server2, vm: vm1)

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
        def backup1 = new Backup(server: server1, vm: vm1)
        def backup2 = new Backup(server: server1, vm: vm2)

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
        def oldBackup = new Backup(
                server: server1,
                vm: vm1,
                fileLimit: 1
        )

        def newBackup = new Backup(
                server: server1,
                vm: vm1,
                fileLimit: 10
        )

        and:
        service.add(oldBackup)

        when:
        service.update(newBackup)

        then:
        service.getBackups() == [newBackup]
        service.getBackups().first().fileLimit == 10
    }
}
