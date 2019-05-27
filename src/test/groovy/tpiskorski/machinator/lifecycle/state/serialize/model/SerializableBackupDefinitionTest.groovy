package tpiskorski.machinator.lifecycle.state.serialize.model

import spock.lang.Specification
import tpiskorski.machinator.lifecycle.state.serialize.model.SerializableBackupDefinition
import tpiskorski.machinator.model.backup.BackupDefinition
import tpiskorski.machinator.model.server.Credentials
import tpiskorski.machinator.model.server.Server
import tpiskorski.machinator.model.vm.VirtualMachine

class SerializableBackupDefinitionTest extends Specification {

    def 'should create serializable backup from backup and convert it back'() {
        given:
        def server = new Server(new Credentials('user', 'password'), 'other', '321')
        def vm = new VirtualMachine(new Server(new Credentials('user', 'password'), 'some', '123'), 'id1')

        and:
        def backup = new BackupDefinition(server, vm)
        backup.startAtDayOfTheMonth = 1
        backup.repeatInDays = 5
        backup.hour = 10
        backup.fileLimit = 2

        when:
        def serializableBackup = new SerializableBackupDefinition(backup)

        and:
        def convertedBackBackup = serializableBackup.toBackup()

        then:
        convertedBackBackup.server == server
        convertedBackBackup.vm == vm
        convertedBackBackup == backup
        convertedBackBackup.startAtDayOfTheMonth == 1
        convertedBackBackup.repeatInDays == 5
        convertedBackBackup.hour == 10
        convertedBackBackup.fileLimit == 2
    }
}
