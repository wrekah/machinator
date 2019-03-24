package com.github.tpiskorski.vboxcm.core.backup

import com.github.tpiskorski.vboxcm.core.server.Server
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine
import spock.lang.Specification

class BackupTest extends Specification {

    def server1 = new Server('some', '123')
    def server2 = new Server('other', '321')

    def vm1 = new VirtualMachine(server1, 'id1')
    def vm2 = new VirtualMachine(server2, 'id1')

    def 'should properly compare equal backups'() {
        given:
        def backup1 = new Backup(server: server1, vm: vm1)
        def backup2 = new Backup(server: server1, vm: vm1)

        expect:
        backup1 == backup2
        backup2 == backup1

        and:
        backup1.hashCode() == backup2.hashCode()
    }

    def 'should properly compare not equal backups'() {
        given:
        def backup1 = new Backup(server: server1, vm: vm1)
        def backup2 = new Backup(server: server2, vm: vm1)
        def backup3 = new Backup(server: server1, vm: vm2)

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
        def backup = new Backup(server: server1, vm: vm1)

        expect:
        something != backup

        and:
        backup != something
    }
}
