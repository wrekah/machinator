package com.github.tpiskorski.vboxcm.config

import com.github.tpiskorski.vboxcm.config.Config
import com.github.tpiskorski.vboxcm.config.ConfigReader
import com.github.tpiskorski.vboxcm.config.PropertiesConfigConverter
import spock.lang.Specification
import spock.lang.Subject

class ConfigReaderTest extends Specification {

    def reader = Mock(ConfigReader.Reader)
    def converter = Mock(PropertiesConfigConverter)

    @Subject configReader = new ConfigReader(
            reader: reader,
            converter: converter
    )

    def 'should read properties and create config'() {
        given:
        def config = new Config()
        def properties = new Properties()

        when:
        def result = configReader.read()

        then:
        result == config

        and:
        1 * reader.read() >> properties
        1 * converter.convert(properties) >> config
    }
}
