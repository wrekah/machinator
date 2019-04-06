package tpiskorski.vboxcm.core.backup

import spock.lang.Specification
import spock.lang.Subject

class BackupDefinitionServiceTest extends Specification {

    def backupRepository = Mock(BackupDefinitionRepository)

    @Subject service = new BackupDefinitionService(backupRepository)

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

    def 'should update backup'() {
        given:
        def updatedBackup = Mock(BackupDefinition)
        def oldBackup = Mock(BackupDefinition)

        when:
        service.update(updatedBackup)

        then:
        1 * backupRepository.find(updatedBackup) >> oldBackup
        4 * oldBackup./set.*/(_)
    }
}
