package com.github.tpiskorski.vboxcm.stub

import com.github.tpiskorski.vboxcm.core.backup.Backup
import com.github.tpiskorski.vboxcm.core.backup.BackupService
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineService
import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class BackupStubGeneratorTest extends Specification {

    def backupService = Mock(BackupService)
    def virtualMachineService = Mock(VirtualMachineService)

    @Subject generator = new BackupStubGenerator(
            backupService, virtualMachineService
    )

    def 'should create backup for vm'() {
        given:
        def server = 'localhost:10'
        def vmName = 'vm1'

        def vm = new VirtualMachine(
                server: server,
                vmName: vmName
        )

        when:
        def backup = generator.createBackupForVm(vm)

        then:
        backup.server == server
        backup.vm == vmName
    }

    @Unroll
    def 'should generate backups for vms'() {
        given:
        def vms = [Mock(VirtualMachine)] * vmNumber as ObservableList

        when:
        generator.afterPropertiesSet()

        then:
        1 * virtualMachineService.getVms() >> vms
        expectedBackups * backupService.add(_ as Backup)

        where:
        vmNumber || expectedBackups
        10       || 5
        5        || 3
        1        || 1
    }
}
