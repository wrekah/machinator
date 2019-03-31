package tpiskorski.vboxcm.command

import spock.lang.Specification
import spock.lang.Subject

class ShowVmInfoParserTest extends Specification {

    @Subject parser = new ShowVmInfoParser()

    def 'should parse showvminfo command'() {
        given:
        def commandResult = Mock(CommandResult)
        commandResult.getStd() >> '''
vram=16
cpuexecutioncap=100
hpet="off"
cpu-profile="host"
chipset="piix3"
firmware="BIOS"
memory=1024
cpus=1
pae="on"
longmode="on"
'''

        when:
        def result = parser.parse(commandResult)

        then:
        result.cpus == 1
        result.memory == 1024
    }
}
