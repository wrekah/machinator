package com.github.tpiskorski.vboxcm.config

import spock.lang.Specification
import spock.lang.Subject

class ConfigWriterTest extends Specification {

    def writer = Mock(ConfigWriter.Writer)
    def converter = Mock(PropertiesConfigConverter)

    @Subject configWriter = new ConfigWriter(
            writer: writer,
            converter: converter
    )

    def 'should write config'() {
        given:
        def config = new Config()
        def properties = new Properties()
        def filePath = 'some/file/path'

        when:

        configWriter.write(filePath, config)

        then:
        1 * converter.convert(config) >> properties
        1 * writer.write(filePath, properties)
    }

}
