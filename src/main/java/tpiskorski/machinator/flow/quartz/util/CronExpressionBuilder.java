package tpiskorski.machinator.flow.quartz.util;

import tpiskorski.machinator.model.backup.BackupDefinition;

public class CronExpressionBuilder {

    public String build(BackupDefinition backupDefinition) {
        return String.format("0 0 %d %d/%d * ?",
            backupDefinition.getHour(),
            backupDefinition.getStartAtDayOfTheMonth(),
            backupDefinition.getRepeatInDays()
        );
    }
}
