package com.autoyol.platformcost

import com.google.common.collect.Lists

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * @author <ahref="mailto:lianglin.sjtu@gmail.com" > AndySjtu</a>
 * @date 2019/12/18 2:12 下午
 *
 * */
class LocalDateTimeUtilTest extends spock.lang.Specification {
    def "ComputeBetweenDays"() {
        expect:
        LocalDateTimeUtil.computeBetweenDays(a,b)==c

        where:
           a << [LocalDateTime.of(2019,12,17,13,10,10),LocalDateTime.of(2019,12,18,10,10,10)]
           b << [LocalDateTime.of(2019,12,18,14,10,10), LocalDateTime.of(2019,12,18,14,10,10)]
           c << [Lists.asList(LocalDate.of(2019,12,17),LocalDate.of(2019,12,18)),Lists.asList(LocalDate.of(2019,12,18))]
    }
}
