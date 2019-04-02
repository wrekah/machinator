package tpiskorski.vboxcm.persistance

import org.springframework.context.ConfigurableApplicationContext
import spock.lang.Specification
import spock.lang.Subject

class ShutdownServiceTest extends Specification {

    def context = Mock(ConfigurableApplicationContext)

    @Subject service = new ShutdownService(context)

    def 'should shutdown'() {
        when:
        service.shutdown()

        then:
        1 * context.close()
    }
}
