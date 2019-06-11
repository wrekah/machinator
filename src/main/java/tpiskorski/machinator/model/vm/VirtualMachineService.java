package tpiskorski.machinator.model.vm;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.lifecycle.quartz.PersistScheduler;
import tpiskorski.machinator.lifecycle.state.manager.PersistenceType;
import tpiskorski.machinator.model.server.Server;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Service
public class VirtualMachineService {

    private final VirtualMachineRepository virtualMachineRepository;
    private final PersistScheduler persistScheduler;

    @Autowired
    public VirtualMachineService(VirtualMachineRepository virtualMachineRepository, PersistScheduler persistScheduler) {
        this.virtualMachineRepository = virtualMachineRepository;
        this.persistScheduler = persistScheduler;
    }

    public void add(VirtualMachine vm) {
        virtualMachineRepository.add(vm);
        persistScheduler.schedulePersistence(PersistenceType.VIRTUAL_MACHINE);
    }

    public void put(VirtualMachine vm){
        virtualMachineRepository.add(vm);
    }

    public void upsert(VirtualMachine vm) {
        Optional<VirtualMachine> optional = virtualMachineRepository.find(vm);
        if (optional.isEmpty()) {
            add(vm);
        } else {
            VirtualMachine virtualMachine = optional.get();

            virtualMachine.setState(vm.getState());
            virtualMachine.setCpuCores(vm.getCpuCores());
            virtualMachine.setRamMemory(vm.getRamMemory());
            virtualMachine.setVmName(vm.getVmName());
        }
    }

    public ObservableList<VirtualMachine> getVms() {
        return virtualMachineRepository.getVms();
    }

    public ObservableList<VirtualMachine> getVms(Server server) {
        return virtualMachineRepository.getVms(server);
    }

    public void remove(VirtualMachine vmToRemove) {
        virtualMachineRepository.remove(vmToRemove);
    }

    public void removeByServer(Server serverToRemove) {
        virtualMachineRepository.removeByServer(serverToRemove);
    }

    public void updateNotReachableBy(Server server) {
        virtualMachineRepository.getVms(server).forEach(vm -> {
            vm.setState(VirtualMachineState.UNREACHABLE);
        });
    }

    public void add(List<VirtualMachine> vms) {
        vms.forEach(this::add);
    }

    public void replace(Server server, List<VirtualMachine> vms) {
        virtualMachineRepository.removeByServer(server);
        add(vms);
    }

    public void upsert(List<VirtualMachine> vms) {
        vms.forEach(this::upsert);
    }

    public void refresh(Server server, List<VirtualMachine> vms) {
        List<VirtualMachine> toRemove = getVmsThatMissingInRefresh(server, vms);

        vms.forEach(this::refresh);
        toRemove.forEach(this::remove);
    }

    private List<VirtualMachine> getVmsThatMissingInRefresh(Server server, List<VirtualMachine> vms) {
        return virtualMachineRepository.getVms().stream()
            .filter(virtualMachine -> virtualMachine.getServer().equals(server))
            .filter(virtualMachine -> virtualMachine.getType() == VirtualMachineType.REGULAR)
            .filter(not(new HashSet<>(vms)::contains))
            .collect(Collectors.toList());
    }

    private void refresh(VirtualMachine virtualMachine) {
        if (virtualMachineRepository.contains(virtualMachine)) {
            update(virtualMachine);
        } else {
            add(virtualMachine);
        }
    }

    private void update(VirtualMachine virtualMachine) {
        virtualMachineRepository.update(virtualMachine);
    }

    public void unreachable(Server server) {
        List<VirtualMachine> vms = virtualMachineRepository.getVms().stream()
            .filter(virtualMachine -> virtualMachine.getServer().equals(server))
            .collect(Collectors.toList());

        vms.forEach(this::unreachable);
    }

    private void unreachable(VirtualMachine virtualMachine) {
        virtualMachine.setState(VirtualMachineState.NODE_NOT_REACHABLE);
    }
}
