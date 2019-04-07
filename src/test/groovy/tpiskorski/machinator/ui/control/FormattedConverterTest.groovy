package tpiskorski.machinator.ui.control

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.LocalDate

class FormattedConverterTest extends Specification {

    @Subject converter = new FormattedConverter()

    @Unroll
    def 'should do the conversion from local data to string'() {
        expect:
        converter.toString(localDate) == expected

        where:
        localDate                 || expected
        null                      || ''
        LocalDate.of(2019, 2, 12) || '2019-02-12'
    }

    def 'should do the conversion from string to local data'() {
        expect:
        converter.fromString(str) == expected

        where:
        str          || expected
        null         || null
        ' '          || null
        '2019-02-12' || LocalDate.of(2019, 2, 12)
    }
}
