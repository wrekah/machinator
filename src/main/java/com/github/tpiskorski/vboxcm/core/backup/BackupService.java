package com.github.tpiskorski.vboxcm.core.backup;

import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BackupService {

    private final BackupRepository backupRepository;

    @Autowired public BackupService(BackupRepository backupRepository) {
        this.backupRepository = backupRepository;
    }

    public ObservableList<Backup> getBackups() {
        return backupRepository.getBackups();
    }

    public void add(Backup backup) {
        backupRepository.add(backup);
    }

    public void remove(Backup backup) {
        backupRepository.remove(backup);
    }
}
