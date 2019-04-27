package tpiskorski.machinator.lifecycle.state.serialize.model

import spock.lang.Specification
import tpiskorski.machinator.model.server.Credentials
import tpiskorski.machinator.model.server.Server
import tpiskorski.machinator.model.vm.VirtualMachine
import tpiskorski.machinator.model.watchdog.Watchdog

class SerializableWatchdogTest extends Specification {

    def 'should create serializable watchdog from watchdog and convert it back'() {
        given:
        def server = new Server(new Credentials('user', 'password'), 'other', '321')
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
