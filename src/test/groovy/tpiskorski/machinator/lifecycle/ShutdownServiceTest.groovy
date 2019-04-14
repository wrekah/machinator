package tpiskorski.machinator.lifecycle

import org.springframework.context.ConfigurableApplicationContext
import spock.lang.Specification
import spock.lang.Subject
import spock.util.concurrent.PollingConditions
import tpiskorski.machinator.lifecycle.state.DefaultAppStatePersister

import java.util.concurrent.CyclicBarrier

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

    def 'should only do shutdown once'() {
        given:
        def barrier = new CyclicBarrier(3)
        def firstThreadRan = false
        def secondThreadRan = false

        Thread.start {
            barrier.await()
            service.shutdown()
            firstThreadRan = true
        }

        Thread.start {
            barrier.await()
            service.shutdown()
            secondThreadRan = true
        }

        when:
        barrier.await()

        then:
        new PollingConditions(timeout: 5, delay: 1).eventually {
            assert firstThreadRan
            assert secondThreadRan
        }

        then:
        1 * appStatePersister.persist()
        1 * context.close()
    }
}
