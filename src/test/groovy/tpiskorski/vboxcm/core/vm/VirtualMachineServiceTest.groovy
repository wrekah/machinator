package tpiskorski.vboxcm.core.vm

import tpiskorski.vboxcm.core.server.Server
import javafx.collections.ObservableList
import spock.lang.Specification
import spock.lang.Subject

class VirtualMachineServiceTest extends Specification {

    def virtualMachineRepository = Mock(VirtualMachineRepository)

    @Subject service = new VirtualMachineService(virtualMachineRepository)

    def 'should add vm'() {
        given:
        def vm = Mock(VirtualMachine)

        when:
        service.add(vm)

        then:
        1 * virtualMachineRepository.add(vm)
    }

    def 'should get vms'() {
        when:
        service.getVms()

        then:
        1 * virtualMachineRepository.getVms()
    }

    def 'should get vms by server'() {
        given:
        def server = Mock(Server)

        when:
        service.getVms(server)

        then:
        1 * virtualMachineRepository.getVms(server)
    }

    def 'should remove vm'() {
        given:
        def vm = Mock(VirtualMachine)

        when:
        service.remove(vm)

        then:
        1 * virtualMachineRepository.remove(vm)
    }

    def 'should remove vm by server'() {
        given:
        def server = Mock(Server)

        when:
        service.removeByServer(server)

        then:
        1 * virtualMachineRepository.removeByServer(server)
    }

    def 'should update not reachable by server'() {
        given:
        def server = Mock(Server)
        def vms = [Mock(VirtualMachine), Mock(VirtualMachine)] as ObservableList

        when:
        service.updateNotReachableBy(server)

        then:
        1 * virtualMachineRepository.getVms(server) >> vms
        vms.each { 1 * it.setState(VirtualMachineState.UNREACHABLE) }
    }

    def 'should add list of vms'() {
        given:
        def vm1 = Mock(VirtualMachine)
        def vm2 = Mock(VirtualMachine)

        when:
        service.add([vm1, vm2])

        then:
        2 * virtualMachineRepository.add(_ as VirtualMachine)
    }

    def 'should replace vms on given server'() {
        given:
        def server = Mock(Server)
        def vms = [Mock(VirtualMachine), Mock(VirtualMachine)]

        when:
        service.replace(server, vms)

        then:
        1 * virtualMachineRepository.removeByServer(server)
        2 * virtualMachineRepository.add(_ as VirtualMachine)
    }

    def 'should invoke update for the number of vms'() {
        given:
        def vms = [Mock(VirtualMachine), Mock(VirtualMachine), Mock(VirtualMachine)]

        when:
        service.upsert(vms)

        then:
        vms.size() * virtualMachineRepository.find(_) >> Optional.empty()
    }

    def 'should add instead of update if no vm is present'() {
        given:
        def vm = Mock(VirtualMachine)

        when:
        service.upsert(vm)

        then:
        1 * virtualMachineRepository.find(vm) >> Optional.empty()
        1 * virtualMachineRepository.add(vm)
    }

    def 'should update existing vm'() {
        given:
        def vm = Mock(VirtualMachine)
        def existingVm = Mock(VirtualMachine)

        when:
        service.upsert(vm)

        then:
        1 * virtualMachineRepository.find(vm) >> Optional.of(existingVm)
        1 * existingVm.setState(_)
        1 * existingVm.setCpuCores(_)
        1 * existingVm.setRamMemory(_)
        1 * existingVm.setVmName(_)
    }
}
