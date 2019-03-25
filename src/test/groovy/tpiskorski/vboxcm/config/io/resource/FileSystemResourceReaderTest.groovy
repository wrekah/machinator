package tpiskorski.vboxcm.config.io.resource

import org.springframework.core.io.FileSystemResourceLoader
import org.springframework.core.io.Resource
import spock.lang.Specification
import spock.lang.Subject

class FileSystemResourceReaderTest extends Specification {

    def propertiesReader = Mock(PropertiesReader)
    def fileSystemResourceLoader = Mock(FileSystemResourceLoader)

    @Subject reader = new FileSystemResourceReader(
            propertiesReader: propertiesReader,
            fileSystemResourceLoader: fileSystemResourceLoader
    )

    def 'should get resource and read properties'() {
        given:
        def filePath = 'some/file/path'
        def resource = Mock(Resource)

        when:
        reader.read(filePath)

        then:
        1 * fileSystemResourceLoader.getResource(filePath) >> resource
        1 * propertiesReader.read(resource)
    }
}
