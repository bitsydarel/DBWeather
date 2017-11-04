/*
 *  Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweather.viewmodels.news

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.ZonedDateTime
import org.junit.Assert.*

/**
 * Created by darel on 28.10.17.
 *
 * Weather Mapper Test
 */
@RunWith(JUnit4::class)
class WeatherMapperKtTest {

    @Test
    fun `Testing sorting of day weather by time`() {
        val currentTime = ZonedDateTime.now()
        val tomorrow = currentTime.plusDays(1).toInstant().toEpochMilli()
        val afterTomorrow = currentTime.plusDays(2).toInstant().toEpochMilli()
        val nextDayAfterTomorrow = currentTime.plusDays(3).toInstant().toEpochMilli()

        val days = listOf(tomorrow, currentTime.toInstant().toEpochMilli(), nextDayAfterTomorrow, afterTomorrow).sorted()

        assertEquals(currentTime.toInstant().toEpochMilli(), days[0])
        assertEquals(tomorrow, days[1])
        assertEquals(afterTomorrow, days[2])
        assertEquals(nextDayAfterTomorrow, days[3])
    }
}