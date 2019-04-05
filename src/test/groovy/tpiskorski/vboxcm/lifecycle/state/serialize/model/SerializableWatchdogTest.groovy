package tpiskorski.vboxcm.lifecycle.state.serialize.model

import spock.lang.Specification
import tpiskorski.vboxcm.core.server.Server
import tpiskorski.vboxcm.core.vm.VirtualMachine
import tpiskorski.vboxcm.core.watchdog.Watchdog

class SerializableWatchdogTest extends Specification {

    def 'should create serializable watchdog from watchdog and convert it back'() {
        given:
        def server = new Server('other', '321')
        def vm = new VirtualMachine(new Server('some', '123'), 'id1')
        def watchdog = new Watchdog(vm, server)

        when:
        def serializableWatchdog = new SerializableWatchdog(watchdog)

        and:
        def convertedBackWatchdog = serializableWatchdog.toWatchdog()

        then:
        convertedBackWatchdog.watchdogServer == server
        convertedBackWatchdog.virtualMachine == vm
        convertedBackWatchdog == watchdog
    }
}
