package tpiskorski.vboxcm.core.backup

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.vm.VirtualMachine

class BackupDefinitionRepositoryTest extends Specification {

    @Subject repository = new BackupDefinitionRepository()

    def server1 = new Server('some', '123')
    def server2 = new Server('other', '321')

    def vm1 = new VirtualMachine(server1, 'id1')
    def vm2 = new VirtualMachine(server2, 'id1')

    def 'should add backups'() {
        given:
        def backup1 = new BackupDefinition(server1, vm1)
        def backup2 = new BackupDefinition(server2, vm1)

        when:
        repository.add(backup1)
        repository.add(backup2)

        then:
        repository.getBackups() == [backup1, backup2]
    }

    def 'should remove backup'() {
        given:
        def backup1 = new BackupDefinition(server1, vm1)

        when:
        repository.add(backup1)
        repository.remove(backup1)

        then:
        repository.getBackups().empty
    }

    def 'should add and remove backups'() {
        given:
        def backup1 = new BackupDefinition(server1, vm1)
        def backup2 = new BackupDefinition(server2, vm1)
        def backup3 = new BackupDefinition(server1, vm2)

        when:
        repository.add(backup1)
        repository.add(backup2)
        repository.add(backup3)

        then:
        repository.getBackups() == [backup1, backup2, backup3]

        when:
        repository.remove(backup1)

        then:
        repository.getBackups() == [backup2, backup3]

        when:
        repository.remove(backup2)

        then:
        repository.getBackups() == [backup3]

        when:
        repository.remove(backup3)

        then:
        repository.getBackups().empty
    }
}
