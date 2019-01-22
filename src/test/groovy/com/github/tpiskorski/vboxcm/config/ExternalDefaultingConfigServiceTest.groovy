package com.github.tpiskorski.vboxcm.config

import com.github.tpiskorski.vboxcm.config.io.ExternalConfigLoader
import com.github.tpiskorski.vboxcm.config.io.InternalConfigLoader
import com.github.tpiskorski.vboxcm.config.io.exception.ConfigNotFoundException
import spock.lang.Specification
import spock.lang.Subject

class ExternalDefaultingConfigServiceTest extends Specification {

    def externalConfigLoader = Mock(ExternalConfigLoader)
    def internalConfigLoader = Mock(InternalConfigLoader)

    @Subject service = new ExternalDefaultingConfigService(
            externalConfigLoader,
            internalConfigLoader
    )

    def 'should load external config'() {
        when:
        service.loadConfig()

        then:
        1 * externalConfigLoader.loadExternalConfig()
    }

    def 'should load internal config if cannot load external config'() {
        when:
        service.loadConfig()

        then:
        1 * externalConfigLoader.loadExternalConfig() >> { throw new ConfigNotFoundException() }
        1 * internalConfigLoader.loadInternalConfig()
    }


}
