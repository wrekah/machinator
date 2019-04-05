package tpiskorski.vboxcm.shutdown.state.persist

import spock.lang.Specification
import tpiskorski.vboxcm.core.backup.Backup
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.vm.VirtualMachine

class SerializableBackupTest extends Specification {

    def 'should create serializable backup from backup and convert it back'() {
        given:
        def server = new Server('other', '321')
        def vm = new VirtualMachine(new Server('some', '123'), 'id1')
        def backup = new Backup(server, vm)

        when:
        def serializableBackup = new SerializableBackup(backup)

        and:
        def convertedBackBackup = serializableBackup.toBackup()

        then:
        convertedBackBackup.server == server
        convertedBackBackup.vm == vm
        convertedBackBackup == backup
    }
}
