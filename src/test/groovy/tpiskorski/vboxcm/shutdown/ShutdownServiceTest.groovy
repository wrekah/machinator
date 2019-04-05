package tpiskorski.vboxcm.shutdown

import org.springframework.context.ConfigurableApplicationContext
import spock.lang.Specification
import spock.lang.Subject
import tpiskorski.vboxcm.shutdown.state.persist.DefaultAppStatePersister

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
