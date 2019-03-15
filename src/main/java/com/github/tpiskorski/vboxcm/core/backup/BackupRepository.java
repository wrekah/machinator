package com.github.tpiskorski.vboxcm.core.backup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Repository;

@Repository
public class BackupRepository {

    private final ObservableList<Backup> jobObservableList = FXCollections.observableArrayList(Backup.extractor());

    void add(Backup backup) {
        jobObservableList.add(backup);
    }

    ObservableList<Backup> getBackups() {
        return jobObservableList;
    }

    void remove(Backup backup) {
        jobObservableList.remove(backup);
    }

    Backup find(Backup backup) {
        return jobObservableList.filtered(
            oldBackup -> oldBackup.equals(backup)
        ).get(0);
    }
}
