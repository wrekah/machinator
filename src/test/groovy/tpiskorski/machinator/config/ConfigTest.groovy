package tpiskorski.machinator.config

import spock.lang.Specification

class ConfigTest extends Specification {

    def 'should create default config which has all fields initialized'() {
        when:
        def config = Config.createDefault()

        then:
        for (property in config.getProperties()) {
            assert config."${property.key}" != null
        }
    }

    def 'should copy config'() {
        given:
        def config = Config.createDefault()

        when:
        def copy = Config.copy(config)

        then:
        config.getBackupLocation() == copy.getBackupLocation()
        config.getPollInterval() == copy.getPollInterval()
    }
}
