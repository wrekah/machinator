package tpiskorski.vboxcm.shutdown.state.persist

import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.vboxcm.core.backup.Backup
import tpiskorski.vboxcm.core.backup.BackupService
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.vm.VirtualMachine

class BackupPersisterTest extends Specification {

    def backupService = Mock(BackupService)
    def objectPersister = Mock(ObjectPersister)

    @Subject persister = new BackupPersister(backupService)

    def setup() {
        persister.objectPersister = objectPersister
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

    def createBackups() {
        def server1 = new Server('some', '123')
        def server2 = new Server('other', '321')

        def vm1 = new VirtualMachine(server1, 'id1')
        def vm2 = new VirtualMachine(server2, 'id1')

        [
                new Backup(server1, vm1),
                new Backup(server2, vm1),
                new Backup(server1, vm2)
        ] as ObservableList
    }
}
