package tpiskorski.machinator.core.vm;

import tpiskorski.machinator.core.server.Server;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VirtualMachineService {

    private final VirtualMachineRepository virtualMachineRepository;

    @Autowired public VirtualMachineService(VirtualMachineRepository virtualMachineRepository) {
        this.virtualMachineRepository = virtualMachineRepository;
    }

    public void add(VirtualMachine vm) {
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

    public void refresh(List<VirtualMachine> vms) {
        vms.forEach(this::refresh);
    }

    private void refresh(VirtualMachine virtualMachine) {
        if(virtualMachineRepository.contains(virtualMachine)){
            update(virtualMachine);
        }else{
            add(virtualMachine);
        }
    }

    private void update(VirtualMachine virtualMachine) {
        virtualMachineRepository.update(virtualMachine);
    }
}
