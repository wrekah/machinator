package tpiskorski.machinator.lifecycle

import org.springframework.context.ConfigurableApplicationContext
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.machinator.lifecycle.state.DefaultAppStatePersister

class ShutdownServiceTest extends Specification {

    def context = Mock(ConfigurableApplicationContext)
    def appStatePersister = Mock(DefaultAppStatePersister)

    @Subject service = new ShutdownService(context, appStatePersister)

    def 'should shutdown'() {
        when:
        service.shutdown()

        then:
        1 * appStatePersister.persist()
        1 * context.close()
    }
}
