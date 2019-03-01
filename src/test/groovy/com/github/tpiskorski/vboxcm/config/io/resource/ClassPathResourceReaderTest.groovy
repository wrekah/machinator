package com.github.tpiskorski.vboxcm.config.io.resource

import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import spock.lang.Specification
import spock.lang.Subject

class ClassPathResourceReaderTest extends Specification {

    def propertiesReader = Mock(PropertiesReader)
    def defaultResourceLoader = Mock(DefaultResourceLoader)

    @Subject reader = new ClassPathResourceReader(
            propertiesReader: propertiesReader,
            defaultResourceLoader: defaultResourceLoader
    )

    def 'should get resource and read properties'() {
        given:
        def filePath = 'some/file/path'
        def resource = Mock(Resource)

        when:
        reader.read(filePath)

        then:
        1 * defaultResourceLoader.getResource(filePath) >> resource
        1 * propertiesReader.read(resource)
    }
}
