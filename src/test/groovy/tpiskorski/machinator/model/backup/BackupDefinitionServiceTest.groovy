package tpiskorski.machinator.model.backup

import spock.lang.Specification
import spock.lang.Subject

class BackupDefinitionServiceTest extends Specification {

    def backupRepository = Mock(BackupDefinitionRepository)
    def backupScheduler = Mock(BackupScheduler)

    @Subject service = new BackupDefinitionService(
            backupRepository, backupScheduler
    )

    def 'should get backups'() {
        when:
        service.getBackups()

        then:
        1 * backupRepository.getBackups()
    }

    def 'should add backup'() {
        given:
        def backup = Mock(BackupDefinition)

        when:
        service.add(backup)

        then:
        1 * backupRepository.add(backup)
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
