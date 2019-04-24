package tpiskorski.machinator.command

import spock.lang.Specification

class CommandTest extends Specification {

    def 'should get regular parts of command'() {
        given:
        def command = new Command(
                parts: [
                        'sh',
                        '-c',
                        'VBoxManage --version'
                ]
        )

        expect:
        command.getParts() == ['sh', '-c', 'VBoxManage --version']
    }

    def 'should get escaped parts of command'() {
        given:
        def command = new Command(
                parts: [
                        'sh',
                        '-c',
                        'VBoxManage --version'
                ]
        )

        expect:
        command.toEscapedString() == 'sh -c "VBoxManage --version"'
    }
}
