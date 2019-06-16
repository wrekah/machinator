package tpiskorski.machinator.config.io

import tpiskorski.machinator.config.Config
import spock.lang.Specification
import spock.lang.Subject

class PropertiesConfigConverterTest extends Specification {

    @Subject converter = new PropertiesConfigConverter()

    def 'should convert properties to config'() {
        given:
        def properties = new Properties([
                'poll.interval'  : '20',
                'backup.location': 'dev/null'
        ])

        when:
        def config = converter.convert(properties)

        then:
        config.pollInterval == 20
        config.backupLocation == 'dev/null'
    }

    def 'should convert config to properties'() {
        given:
        def config = Config.builder()
                .pollInterval(20)
                .backupLocation('dev/null')
                .build()

        when:
        def properties = converter.convert(config)

        then:
        properties.get('poll.interval') == '20'
        properties.get('backup.location') == 'dev/null'
    }
}
