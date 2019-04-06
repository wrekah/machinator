package tpiskorski.vboxcm.stub.generator;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tpiskorski.vboxcm.core.backup.BackupDefinition;
import tpiskorski.vboxcm.core.backup.BackupDefinitionService;
import tpiskorski.vboxcm.core.vm.VirtualMachine;
import tpiskorski.vboxcm.core.vm.VirtualMachineService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

@Profile("stub")
@DependsOn("virtualMachineStubGenerator")
@Component
public class BackupStubGenerator implements InitializingBean {

    private final BackupDefinitionService backupDefinitionService;

    private final VirtualMachineService virtualMachineService;

    @Autowired public BackupStubGenerator(BackupDefinitionService backupDefinitionService, VirtualMachineService virtualMachineService) {
        this.backupDefinitionService = backupDefinitionService;
        this.virtualMachineService = virtualMachineService;
    }

    @Override public void afterPropertiesSet() {
        ObservableList<VirtualMachine> vms = virtualMachineService.getVms();

        int size = vms.size();
        IntStream.range(0, size)
            .filter(halfWithAtLeastOne(size))
            .mapToObj(vms::get)
            .map(this::createBackupForVm)
            .forEach(backupDefinitionService::add);
    }

    private IntPredicate halfWithAtLeastOne(int size) {
        return n -> {
            if (size > 2) {
                return n % 2 == 0;
            } else {
                return true;
            }
        };
    }

    BackupDefinition createBackupForVm(VirtualMachine virtualMachine) {
        BackupDefinition backupDefinition = new BackupDefinition(virtualMachine.getServer(), virtualMachine);

        backupDefinition.setFileLimit(3);
        backupDefinition.setCurrentFiles(ThreadLocalRandom.current().nextInt(0, backupDefinition.getFileLimit() + 1));
        backupDefinition.setFrequency(10);
        backupDefinition.setBackupTime(LocalTime.of(12, 0));
        backupDefinition.setFirstBackupDay(LocalDate.of(2019, 1, 1));

        return backupDefinition;
    }
}
