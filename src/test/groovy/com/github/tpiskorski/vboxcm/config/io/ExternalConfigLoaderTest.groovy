package com.github.tpiskorski.vboxcm.config.io

import com.github.tpiskorski.vboxcm.config.Config
import spock.lang.Specification
import spock.lang.Subject

class ExternalConfigLoaderTest extends Specification {

    static configPath = 'some/config/path'

    def configWriter = Mock(ConfigWriter)
    def configReader = Mock(ConfigReader)

    def fileChecker = Mock(FileChecker)

    @Subject loader = new ExternalConfigLoader(
            configWriter,
            configReader
    )

    def setup() {
        loader.fileChecker = fileChecker
        loader.externalConfigFilePath = configPath
    }

    def 'should read existing config'() {
        when:
        loader.loadExternalConfig()

        then:
        1 * fileChecker.notExists(configPath) >> false
        1 * configReader.read(configPath)
    }

    def 'should create default config if does not exist then read it'() {
        when:
        loader.loadExternalConfig()

        then:
        1 * fileChecker.notExists(configPath) >> true
        1 * configWriter.write(configPath, _ as Config)
        1 * configReader.read(configPath)
    }

}
