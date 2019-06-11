package tpiskorski.machinator.lifecycle.state.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tpiskorski.machinator.lifecycle.state.serialize.model.SerializableVirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VirtualMachineStateManager extends StateManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineStateManager.class);

    private final VirtualMachineService virtualMachineService;

    @Autowired
    public VirtualMachineStateManager(VirtualMachineService virtualMachineService) {
        this.virtualMachineService = virtualMachineService;
    }

    @Override public String getPersistResourceFileName() {
        return "./data/vms.dat";
    }

    @Override public PersistenceType getPersistenceType() {
        return PersistenceType.VIRTUAL_MACHINE;
    }

    @Override public void persist() {
        LOGGER.info("Starting vms persistence");

        List<SerializableVirtualMachine> toSerialize = virtualMachineService.getVms().stream()
            .map(SerializableVirtualMachine::new)
            .collect(Collectors.toList());

        try {
            objectPersister.persist(getPersistResourceFileName(), toSerialize);
            LOGGER.info("Persisted vms!");
        } catch (IOException ex) {
            LOGGER.error("Could not persist vms", ex);
        }
    }

    @Override public void restore() {
        LOGGER.info("Starting restoring vms state");

        try {
            List<SerializableVirtualMachine> restoredVms = objectRestorer.restore(getPersistResourceFileName());

            LOGGER.info("Restoring {} vms", restoredVms.size());

            restoredVms.stream()
                .map(SerializableVirtualMachine::toVirtualMachine)
                .collect(Collectors.toList())
                .forEach(virtualMachineService::put);

            LOGGER.info("Vms state restored");
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.error("Could not restore vms state", ex);
        }
    }
}
