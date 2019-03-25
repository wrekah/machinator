package tpiskorski.vboxcm.core.backup

import spock.lang.Specification
import spock.lang.Subject

class BackupServiceTest extends Specification {

    def backupRepository = Mock(BackupRepository)

    @Subject service = new BackupService(backupRepository)

    def 'should get backups'() {
        when:
        service.getBackups()

        then:
        1 * backupRepository.getBackups()
    }

    def 'should add backup'() {
        given:
        def backup = Mock(Backup)

        when:
        service.add(backup)

        then:
        1 * backupRepository.add(backup)
    }

    def 'should remove backup'() {
        given:
        def backup = Mock(Backup)

        when:
        service.remove(backup)

        then:
        1 * backupRepository.remove(backup)
    }

    def 'should update backup'() {
        given:
        def updatedBackup = Mock(Backup)
        def oldBackup = Mock(Backup)

        when:
        service.update(updatedBackup)

        then:
        1 * backupRepository.find(updatedBackup) >> oldBackup
        4 * oldBackup./set.*/(_)
    }
}
