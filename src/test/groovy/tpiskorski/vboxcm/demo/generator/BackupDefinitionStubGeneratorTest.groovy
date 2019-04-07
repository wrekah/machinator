package tpiskorski.vboxcm.demo.generator

import tpiskorski.vboxcm.core.backup.BackupDefinition
import tpiskorski.vboxcm.core.backup.BackupDefinitionService
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.vm.VirtualMachine
import tpiskorski.vboxcm.core.vm.VirtualMachineService
import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class BackupDefinitionStubGeneratorTest extends Specification {

    def backupService = Mock(BackupDefinitionService)
    def virtualMachineService = Mock(VirtualMachineService)

    @Subject generator = new DemoBackupGenerator(
            backupService, virtualMachineService
    )

    def 'should create backup for vm'() {
        given:
        def server = new Server('localhost', '10')
        def vm = new VirtualMachine(server, 'id1')

        when:
        def backup = generator.createBackupForVm(vm)

        then:
        backup.server == server
        backup.vm == vm
        backup.currentFiles <= backup.fileLimit
    }

    @Unroll
    def 'should generate backups for vms'() {
        given:
        def vms = [Mock(VirtualMachine)] * vmNumber as ObservableList

        when:
        generator.afterPropertiesSet()

        then:
        1 * virtualMachineService.getVms() >> vms
        expectedBackups * backupService.add(_ as BackupDefinition)

        where:
        vmNumber || expectedBackups
        10       || 5
        5        || 3
        1        || 1
    }
}
