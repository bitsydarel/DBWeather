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

import com.dbeginc.dbweatherdomain.entities.news.Article
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.util.regex.Pattern

/**
 * Created by darel on 10.10.17.
 *
 * Testing Mapping of domain entities to presentation entities
 */
@RunWith(JUnit4::class)
class NewsMapperKtTest {

    @Test
    fun `Testing convert from domain model to presentation model`() {
        val currentTime = ZonedDateTime.now()

        val articleWith5Minute = Article("","", "", "", null, currentTime.minusMinutes(5).toInstant().toString(), "").toTestModel("Darel", currentTime)
        val articleWith2Hours = Article("","", "", "", null, currentTime.minusHours(2).toInstant().toString(), "").toTestModel("Darel", currentTime)
        val articleWith1day = Article("","", "", "", null, currentTime.minusDays(1).toInstant().toString(), "").toTestModel("Darel", currentTime)

        assertTrue("expected value containing m but got ${articleWith5Minute.publishedAt}", articleWith5Minute.publishedAt!!.contains("m"))

        assertTrue("expected value containing h but got ${articleWith2Hours.publishedAt}", articleWith2Hours.publishedAt!!.contains("h"))

        assertTrue("expected value containing d but got ${articleWith1day.publishedAt}", articleWith1day.publishedAt!!.contains("d"))

    }

    @Test
    fun `Testing sorting of article by publish time`() {
        val currentTime = ZonedDateTime.now()

        val articleWith2Minutes = Article("","", null, "", null, currentTime.minusMinutes(2).toInstant().toString(), "").toTestModel("Darel", currentTime)
        val articleWith5Hours = Article("","", null, "", null, currentTime.minusHours(5).toInstant().toString(), "").toTestModel("Darel", currentTime)
        val articleWith2Days = Article("","", null, "", null, currentTime.minusDays(2).toInstant().toString(), "").toTestModel("Darel", currentTime)
        val articleWith3days = Article("","",null,"", null, currentTime.minusDays(3).toInstant().toString(),"").toTestModel("Darel", currentTime)
        val articleWithNoTime = Article("","",null,"", null,null,"").toTestModel("Darel", currentTime)

        //Testing equality
        assertEquals(articleWith2Minutes.compareTo(articleWith2Minutes), 0)
        // Testing min timestamp and hour
        assertEquals(articleWith2Minutes.compareTo(articleWith5Hours), -1)
        // Testing articleWith5Hours timestamp and min
        assertEquals(articleWith5Hours.compareTo(articleWith2Minutes), 1)
        // Testing articleWith5Hours timestamp and days
        assertEquals(articleWith5Hours.compareTo(articleWith2Days), -1)
        // Testing days timestamp and hour
        assertEquals(articleWith2Days.compareTo(articleWith5Hours), 1)
        // Testing day1 less than day2
        assertEquals(articleWith2Days.compareTo(articleWith3days), -1)
        // Testing day2 more than day1
        assertEquals(articleWith3days.compareTo(articleWith2Days), 1)
        // Testing that no value are more
        assertEquals(articleWith3days.compareTo(articleWithNoTime), 1)
    }

    @Test
    fun `Testing sorting of articles in collections`() {
        val currentTime = ZonedDateTime.now()

        val articleWith2Minutes = Article("","", null, "", null, currentTime.minusMinutes(2).toInstant().toString(), "").toTestModel("Darel", currentTime)
        val articleWith5Minutes = Article("","", null, "", null, currentTime.minusHours(5).toInstant().toString(), "").toTestModel("Darel", currentTime)
        val articleWith5Hours = Article("","", null, "", null, currentTime.minusHours(5).toInstant().toString(), "").toTestModel("Darel", currentTime)
        val articleWith2Days = Article("","", null, "", null, currentTime.minusDays(2).toInstant().toString(), "").toTestModel("Darel", currentTime)
        val articleWith3days = Article("","",null,"", null, currentTime.minusDays(3).toInstant().toString(),"").toTestModel("Darel", currentTime)
        val articleWith5Days = Article("","",null,"", null, currentTime.minusDays(5).toInstant().toString(),"").toTestModel("Darel", currentTime)
        val articleWithNoTime = Article("","",null,"", null,null,"").toTestModel("Darel", currentTime)

        val sortedArticles = listOf(articleWith2Days, articleWith2Minutes, articleWith5Hours, articleWith3days, articleWith5Minutes, articleWith5Days, articleWithNoTime).sorted()

        assertEquals(sortedArticles[0], articleWith2Minutes)
        assertEquals(sortedArticles[1], articleWith5Minutes)
        assertEquals(sortedArticles[2], articleWith5Hours)
        assertEquals(sortedArticles[3], articleWithNoTime)
        assertEquals(sortedArticles[4], articleWith2Days)
        assertEquals(sortedArticles[5], articleWith3days)
        assertEquals(sortedArticles[6], articleWith5Days)

    }

    @Test
    fun `Testing duration between two algo`() {
        val startOfFirst = Instant.now()

        val result = listOf("12m", "23h", "2d", "7d", "30m").map {
            val temp = it.split("").filter { it.isNotEmpty() }.groupBy { it.toIntOrNull() is Int }.values
            temp.first().joinToString(separator="").toInt().to(temp.elementAt(1).joinToString())
        }

        val endOfFirst = Instant.now()

        val result1 =listOf("12m", "23h", "2d", "7d", "30m").map {
            it.split(regex = Pattern.compile("\\D"))
                    .first { it.isNotEmpty() }
                    .toInt()
                    .to(it.split(regex=Pattern.compile("\\d")).first { it.isNotEmpty() })
        }

        val startOfSecond = Instant.now()

        val timeSpend = Duration.between(startOfFirst, endOfFirst).to(Duration.between(endOfFirst, startOfSecond))

        assertEquals(result, result1)
        // Regex solution is more faster than group by solution
        assertTrue("Regex is not faster than group By solution", timeSpend.second.toMillis() < timeSpend.first.toMillis())
    }

    private fun Article.toTestModel(unknownAuthor: String, currentTime: ZonedDateTime) : ArticleModel {
        val publishTime: String

        publishTime = if (publishedAt == null) "..." else {
            val duration = Duration.between(currentTime.toInstant(), Instant.parse(publishedAt))
            when {
                duration.days() > 0 -> "${duration.days()}d"
                duration.hours() > 0 -> "${duration.hours()}h"
                else -> "${duration.minutes()}m"
            }
        }
        return ArticleModel(author=author ?: unknownAuthor, title=title, description=description, url=url, urlToImage=urlToImage, publishedAt=publishTime, sourceId=sourceId)
    }

    private fun Duration.days() : Int = if (isNegative) toDays().unaryMinus().toInt() else toDays().toInt()

    private fun Duration.hours() : Int = if (isNegative) toHours().unaryMinus().toInt() else toHours().toInt()

    private fun Duration.minutes() : Int = if (isNegative) toMinutes().unaryMinus().toInt() else toMinutes().toInt()

}