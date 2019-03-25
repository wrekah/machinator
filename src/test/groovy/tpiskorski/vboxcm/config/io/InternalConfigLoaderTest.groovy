package tpiskorski.vboxcm.config.io

import spock.lang.Specification
import spock.lang.Subject

class InternalConfigLoaderTest extends Specification {

    def configReader = Mock(ConfigReader)

    @Subject loader = new InternalConfigLoader(configReader)

    def 'should load internal config'() {
        given:
        def config = 'config/path'
        loader.internalConfigFilePath = config

        when:
        loader.loadInternalConfig()

        then:
        1 * configReader.read(config)
    }
}
