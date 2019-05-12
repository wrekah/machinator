package tpiskorski.machinator.model.vm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import tpiskorski.machinator.model.server.Server;

import java.util.Optional;

@Repository
public class VirtualMachineRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineRepository.class);

    private ObservableList<VirtualMachine> vmObservableList = FXCollections.observableArrayList(VirtualMachine.extractor());

    void add(VirtualMachine vm) {
        vmObservableList.add(vm);
    }

    ObservableList<VirtualMachine> getVms() {
        return vmObservableList;
    }

    void remove(VirtualMachine vm) {
        vmObservableList.remove(vm);
    }

    ObservableList<VirtualMachine> getVms(Server server) {
        return vmObservableList.filtered(
            virtualMachine -> virtualMachine.getServer().equals(server)
        );
    }

    void removeByServer(Server serverToRemove) {
        vmObservableList.removeIf(virtualMachine -> virtualMachine.getServer().equals(serverToRemove));
    }

    Optional<VirtualMachine> find(VirtualMachine vm) {
        return vmObservableList.stream()
            .filter(virtualMachine -> virtualMachine.equals(vm))
            .findFirst();
    }

    public boolean contains(VirtualMachine virtualMachine) {
        return vmObservableList.contains(virtualMachine);
    }

    public void update(VirtualMachine virtualMachine) {
        VirtualMachine vm = find(virtualMachine).get();

        if (virtualMachine.deepEquals(vm)) {
            return;
        }

        boolean locked = vm.tryLocking();

        if (locked) {
            vm.setState(virtualMachine.getState());
            vm.setCpuCores(virtualMachine.getCpuCores());
            vm.setRamMemory(virtualMachine.getRamMemory());
            vm.setVmName(virtualMachine.getVmName());

            vm.unlock();
        } else {
            LOGGER.info("Skipping {} vm because work is in progress", vm.getId());
        }
    }
}
