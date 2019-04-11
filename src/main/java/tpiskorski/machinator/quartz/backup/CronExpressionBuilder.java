package tpiskorski.machinator.quartz.backup;

import tpiskorski.machinator.core.backup.BackupDefinition;

public class CronExpressionBuilder {

    public String build(BackupDefinition backupDefinition) {
        return String.format("0 %d %d 1/%d * ?",
            backupDefinition.getBackupTime().getMinute(),
            backupDefinition.getBackupTime().getHour(),
            backupDefinition.getFrequency()
        );
    }
}
