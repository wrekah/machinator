package com.github.tpiskorski.vboxcm.stub.generator;

import com.github.tpiskorski.vboxcm.core.backup.Backup;
import com.github.tpiskorski.vboxcm.core.backup.BackupService;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineService;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

@Profile("stub")
@DependsOn("virtualMachineStubGenerator")
@Component
public class BackupStubGenerator implements InitializingBean {

    private final BackupService backupService;

    private final VirtualMachineService virtualMachineService;

    @Autowired public BackupStubGenerator(BackupService backupService, VirtualMachineService virtualMachineService) {
        this.backupService = backupService;
        this.virtualMachineService = virtualMachineService;
    }

    @Override public void afterPropertiesSet() {
        ObservableList<VirtualMachine> vms = virtualMachineService.getVms();

        int size = vms.size();
        IntStream.range(0, size)
            .filter(halfWithAtLeastOne(size))
            .mapToObj(vms::get)
            .map(this::createBackupForVm)
            .forEach(backupService::add);
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

    Backup createBackupForVm(VirtualMachine virtualMachine) {
        Backup backup = new Backup();

        backup.setServer(virtualMachine.getServer());
        backup.setVm(virtualMachine.getVmName());
        backup.setFileLimit(3);
        backup.setFrequency(10);
        backup.setBackupTime(LocalTime.of(12, 0));
        backup.setFirstBackupDay(LocalDate.of(2019, 1, 1));

        return backup;
    }
}
