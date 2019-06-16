package tpiskorski.machinator.lifecycle.state.manager

import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.lifecycle.state.serialize.io.ObjectPersister
import tpiskorski.machinator.lifecycle.state.serialize.io.ObjectRestorer
import tpiskorski.machinator.lifecycle.state.serialize.model.SerializableBackupDefinition
import tpiskorski.machinator.model.backup.BackupDefinition
import tpiskorski.machinator.model.backup.BackupDefinitionService
import tpiskorski.machinator.model.server.Credentials
import tpiskorski.machinator.model.server.Server
import tpiskorski.machinator.model.vm.VirtualMachine

class BackupDefinitionStateManagerTest extends Specification {

    def backupService = Mock(BackupDefinitionService)

    def objectPersister = Mock(ObjectPersister)
    def objectRestorer = Mock(ObjectRestorer)

    @Subject persister = new BackupStateManager(backupService)

    def setup() {
        persister.objectPersister = objectPersister
        persister.objectRestorer = objectRestorer
    }

    def 'should persist backups state'() {
        given:
        def backups = createBackups()

        when:
        persister.persist()

        then:
        1 * backupService.getBackups() >> backups
        1 * objectPersister.persist(_, _)
    }

    def 'should restore backups state'() {
        given:
        def watchdogs = createSerializableBackups()

        when:
        persister.restore()

        then:
        1 * objectRestorer.restore(_) >> watchdogs
        3 * backupService.put(_)
    }

    def 'should not restore anything if io exception is thrown'() {
        when:
        persister.restore()

        then:
        1 * objectRestorer.restore(_) >> { throw new IOException() }
        0 * _
    }

    def 'should not restore anything if class not found exception is thrown'() {
        when:
        persister.restore()

        then:
        1 * objectRestorer.restore(_) >> { throw new ClassNotFoundException() }
        0 * _
    }

    def createBackups() {
        def server1 = new Server(new Credentials("user", "pw"), 'some', '123')
        def server2 = new Server(new Credentials("user", "pw"), 'other', '321')

        def vm1 = new VirtualMachine(server1, 'id1')
        def vm2 = new VirtualMachine(server2, 'id1')

        [
                new BackupDefinition(server1, vm1),
                new BackupDefinition(server2, vm1),
                new BackupDefinition(server1, vm2)
        ] as ObservableList
    }

    def createSerializableBackups() {
        createBackups().collect { new SerializableBackupDefinition(it) }
    }
}
