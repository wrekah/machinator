package com.github.tpiskorski.vboxcm.core.backup

import spock.lang.Specification

class BackupTest extends Specification {

    def 'should properly compare equal backups'() {
        given:
        def backup1 = new Backup(server: 'server1', vm: 'vm1')
        def backup2 = new Backup(server: 'server1', vm: 'vm1')

        expect:
        backup1 == backup2
        backup1.hashCode() == backup2.hashCode()
    }

    def 'should properly compare not equal backups'() {
        given:
        def backup1 = new Backup(server: 'server1', vm: 'vm1')
        def backup2 = new Backup(server: 'server2', vm: 'vm1')
        def backup3 = new Backup(server: 'server1', vm: 'vm2')

        expect:
        backup1 != backup2
        backup2 != backup3
        backup1 != backup3
    }
}
