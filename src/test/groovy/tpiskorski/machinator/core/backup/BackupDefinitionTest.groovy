package tpiskorski.machinator.core.backup

import spock.lang.Specification
import tpiskorski.machinator.core.server.Server
import tpiskorski.machinator.core.vm.VirtualMachine

class BackupDefinitionTest extends Specification {

    def server1 = new Server('some', '123')
    def server2 = new Server('other', '321')

    def vm1 = new VirtualMachine(server1, 'id1')
    def vm2 = new VirtualMachine(server2, 'id1')

    def 'should properly compare equal backups'() {
        given:
        def backup1 = new BackupDefinition(server1, vm1)
        def backup2 = new BackupDefinition(server1, vm1)

        expect:
        backup1 == backup2
        backup2 == backup1

        and:
        backup1.hashCode() == backup2.hashCode()
    }

    def 'should properly compare not equal backups'() {
        given:
        def backup1 = new BackupDefinition(server1, vm1)
        def backup2 = new BackupDefinition(server2, vm1)
        def backup3 = new BackupDefinition(server1, vm2)

        expect:
        backup1 != backup2
        backup2 != backup3
        backup1 != backup3

        and:
        backup2 != backup1
        backup3 != backup2
        backup3 != backup1
    }

    def 'should properly compare not backup'() {
        given:
        def something = new Object()
        def backup = new BackupDefinition(server1, vm1)

        expect:
        something != backup

        and:
        backup != something
    }

    def 'should create not active backup by default'() {
        given:
        def backup = new BackupDefinition(server1, vm1)

        expect:
        !backup.isActive()
    }
}
