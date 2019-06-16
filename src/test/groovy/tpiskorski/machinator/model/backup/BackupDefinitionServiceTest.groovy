package tpiskorski.machinator.model.backup

import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.flow.quartz.backup.BackupScheduler
import tpiskorski.machinator.lifecycle.quartz.PersistScheduler
import tpiskorski.machinator.lifecycle.state.manager.PersistenceType

class BackupDefinitionServiceTest extends Specification {

    def backupRepository = Mock(BackupDefinitionRepository)
    def backupScheduler = Mock(BackupScheduler)
    def persistScheduler = Mock(PersistScheduler)

    @Subject service = new BackupDefinitionService(
            backupRepository, backupScheduler, persistScheduler
    )

    def 'should get backups'() {
        when:
        service.getBackups()

        then:
        1 * backupRepository.getBackups()
    }

    def 'should add backup and schedule persistence'() {
        given:
        def backup = Mock(BackupDefinition)

        when:
        service.add(backup)

        then:
        1 * backupRepository.add(backup)
        1 * persistScheduler.schedulePersistence(PersistenceType.BACKUP_DEFINITION)
    }

    def 'should add backup without scheduling persistence'() {
        given:
        def backup = Mock(BackupDefinition)

        when:
        service.put(backup)

        then:
        1 * backupRepository.add(backup)
        0 * persistScheduler.schedulePersistence(PersistenceType.BACKUP_DEFINITION)
    }

    def 'should remove backup'() {
        given:
        def backup = Mock(BackupDefinition)

        when:
        service.remove(backup)

        then:
        1 * backupRepository.remove(backup)
    }

    def 'should activate backup'() {
        given:
        def backup = Mock(BackupDefinition)

        when:
        service.activate(backup)

        then:
        1 * backupScheduler.addTaskToScheduler(backup)
        1 * backup.setActive(true)
    }

    def 'should deactivate backup'() {
        given:
        def backup = Mock(BackupDefinition)

        when:
        service.deactivate(backup)

        then:
        1 * backupScheduler.removeTaskFromScheduler(backup)
        1 * backup.setActive(false)
    }
}
