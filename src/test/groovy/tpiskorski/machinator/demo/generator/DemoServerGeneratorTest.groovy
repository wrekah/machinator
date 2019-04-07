package tpiskorski.machinator.demo.generator

import tpiskorski.machinator.core.server.Server
import tpiskorski.machinator.core.server.ServerService
import spock.lang.Specification
import spock.lang.Subject

class DemoServerGeneratorTest extends Specification {

    def serverService = Mock(ServerService)

    @Subject generator = new DemoServerGenerator(serverService)

    def 'should generate configured number of servers'() {
        given:
        def serversToGenerate = 5

        when:
        generator.generateServers(serversToGenerate)

        then:
        serversToGenerate * serverService.add(_ as Server)
    }
}
