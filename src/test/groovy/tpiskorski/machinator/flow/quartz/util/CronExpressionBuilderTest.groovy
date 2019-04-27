package tpiskorski.machinator.flow.quartz.util

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.model.backup.BackupDefinition

class CronExpressionBuilderTest extends Specification {

    @Subject builder = new CronExpressionBuilder()

    def 'should build cron expression for given backup definition'() {
        given:
        def backupDefinition = new BackupDefinition(Mock(Server), Mock(VirtualMachine))
        backupDefinition.setRepeatInDays(10)
        backupDefinition.setHour(12)
        backupDefinition.setStartAtDayOfTheMonth(10)

        expect:
        builder.build(backupDefinition) == '0 0 12 10/10 * ?'
    }
}
