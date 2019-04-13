package tpiskorski.machinator.quartz.backup;

import tpiskorski.machinator.core.backup.BackupDefinition;

public class CronExpressionBuilder {

    public String build(BackupDefinition backupDefinition) {
        return String.format("0 0 %d %d/%d * ?",
            backupDefinition.getHour(),
            backupDefinition.getStartAtDayOfTheMonth(),
            backupDefinition.getRepeatInDays()
        );
    }
}
