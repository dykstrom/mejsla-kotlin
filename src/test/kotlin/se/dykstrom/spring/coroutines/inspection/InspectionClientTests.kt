/*
 * Copyright (C) 2018 Johan Dykstrom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.dykstrom.spring.coroutines.inspection

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.web.coroutine.function.client.CoroutineWebClient

@RunWith(MockitoJUnitRunner::class)
class InspectionClientTests {

    private val objectMapper = jacksonObjectMapper()

    private val webClient: CoroutineWebClient = mock()
    private val uriSpec: CoroutineWebClient.RequestHeadersUriSpec<*> = mock()
    private val responseSpec: CoroutineWebClient.CoroutineResponseSpec = mock()

    private val inspectionClient = InspectionClientImpl(webClient)

    private val record0 = Inspection(id = "00000", streetAddress = "Birger Jarlsgatan 10")
    private val record1 = Inspection(id = "11111", streetAddress = "Klacktorget 4")

    private val singleRecord = objectMapper.writeValueAsString(InspectionEnvelope(records = listOf(record0), totalRecords = "1"))
    private val twoRecords = objectMapper.writeValueAsString(InspectionEnvelope(records = listOf(record0, record1), totalRecords = "2"))

    @Before
    fun setUp() = runBlocking<Unit> {
        whenever(webClient.get()).thenReturn(uriSpec)
        whenever(uriSpec.uri(anyString())).thenReturn(uriSpec)
        whenever(uriSpec.uri(anyString(), any())).thenReturn(uriSpec)
        whenever(uriSpec.retrieve()).thenReturn(responseSpec)
    }

    @Test
    fun shouldGetSingleInspection() = runBlocking<Unit> {
        whenever(responseSpec.body<String>(any())).thenReturn(singleRecord)

        val records = inspectionClient.getInspections()

        assertThat(records).hasSize(1).contains(record0)
    }

    @Test
    fun shouldGetTwoInspections() = runBlocking<Unit> {
        whenever(responseSpec.body<String>(any())).thenReturn(twoRecords)

        val records = inspectionClient.getInspections()

        assertThat(records).hasSize(2).contains(record0).contains(record1)
    }
}
