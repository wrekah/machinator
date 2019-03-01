package com.github.tpiskorski.vboxcm.config.io

import com.github.tpiskorski.vboxcm.config.Config
import com.github.tpiskorski.vboxcm.config.io.ConfigReader
import com.github.tpiskorski.vboxcm.config.io.resource.ResourceReader
import com.github.tpiskorski.vboxcm.config.io.resource.ResourceReaderFactory
import spock.lang.Specification
import spock.lang.Subject

class ConfigReaderTest extends Specification {

    def resourceReaderFactory = Mock(ResourceReaderFactory)
    def converter = Mock(PropertiesConfigConverter)

    @Subject configReader = new ConfigReader(
            resourceReaderFactory: resourceReaderFactory,
            converter: converter
    )

    def 'should read properties and create config'() {
        given:
        def config = new Config()
        def properties = new Properties()
        def resourceReader = Mock(ResourceReader)
        def filePath = 'some/file/path'

        when:
        def result = configReader.read(filePath)

        then:
        result == config

        and:
        1 * resourceReaderFactory.get(filePath) >> resourceReader
        1 * resourceReader.read(filePath) >> properties
        1 * converter.convert(properties) >> config
    }
}
