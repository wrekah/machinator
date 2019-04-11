package tpiskorski.machinator.demo.generator

import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import tpiskorski.machinator.core.backup.BackupDefinition
import tpiskorski.machinator.core.backup.BackupDefinitionService
import tpiskorski.machinator.core.server.Server
import tpiskorski.machinator.core.vm.VirtualMachine
import tpiskorski.machinator.core.vm.VirtualMachineService

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
