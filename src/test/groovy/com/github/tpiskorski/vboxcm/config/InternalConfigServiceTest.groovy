package com.github.tpiskorski.vboxcm.config

import com.github.tpiskorski.vboxcm.config.io.InternalConfigLoader
import spock.lang.Specification
import spock.lang.Subject

class InternalConfigServiceTest extends Specification {

    def internalConfigLoader = Mock(InternalConfigLoader)

    @Subject service = new InternalConfigService(internalConfigLoader)

    def 'should load internal config'() {
        when:
        service.loadConfig()

        then:
        1 * internalConfigLoader.loadInternalConfig()
    }
}
