package com.github.tpiskorski.vboxcm.config

import spock.lang.Specification

class ConfigTest extends Specification {

    def 'should create default config which has all fields initialized'(){
        when:
        def config = Config.createDefault()

        then:
        for(property in config.getProperties()){
            assert config."${property.key}" != null
        }
    }

}
