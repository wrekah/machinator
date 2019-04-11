package tpiskorski.machinator.quartz.backup

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.core.backup.BackupDefinition

import java.time.LocalTime

class CronExpressionBuilderTest extends Specification {

    @Subject builder = new CronExpressionBuilder()

    def 'should build cron expression for given backup definition'(){
        given:
        def backupDefinition = new BackupDefinition()
        backupDefinition.setFrequency(10)
        backupDefinition.setBackupTime(LocalTime.parse("09:10"))

        expect:
        builder.build(backupDefinition) == '0 10 09 1/10 * ?'

    }
}
