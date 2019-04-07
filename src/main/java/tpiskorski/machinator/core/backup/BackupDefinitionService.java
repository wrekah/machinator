package tpiskorski.machinator.core.backup;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.quartz.backup.BackupScheduler;

@Service
public class BackupDefinitionService {

    private final BackupDefinitionRepository backupDefinitionRepository;
    private final BackupScheduler backupScheduler;

    @Autowired
    public BackupDefinitionService(BackupDefinitionRepository backupDefinitionRepository, BackupScheduler backupScheduler) {
        this.backupDefinitionRepository = backupDefinitionRepository;
        this.backupScheduler = backupScheduler;
    }

    public ObservableList<BackupDefinition> getBackups() {
        return backupDefinitionRepository.getBackups();
    }

    public void add(BackupDefinition backupDefinition) {
        backupDefinitionRepository.add(backupDefinition);
        backupScheduler.addTaskToScheduler(backupDefinition);
    }

    public void remove(BackupDefinition backupDefinition) {
        backupScheduler.removeTaskFromScheduler(backupDefinition);
        backupDefinitionRepository.remove(backupDefinition);
    }

    public void update(BackupDefinition backupDefinition) {
        BackupDefinition backupDefinitionToModify = backupDefinitionRepository.find(backupDefinition);

        backupDefinitionToModify.setFirstBackupDay(backupDefinition.getFirstBackupDay());
        backupDefinitionToModify.setFileLimit(backupDefinition.getFileLimit());
        backupDefinitionToModify.setBackupTime(backupDefinition.getBackupTime());
        backupDefinitionToModify.setFrequency(backupDefinition.getFrequency());
    }

    public void deactivate(BackupDefinition backupToDeactivate) {
        backupToDeactivate.setActive(false);
    }

    public void activate(BackupDefinition backupToDeactivate) {
        backupToDeactivate.setActive(true);
    }
}
