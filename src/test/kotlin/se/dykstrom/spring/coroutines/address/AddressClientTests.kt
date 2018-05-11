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

package se.dykstrom.spring.coroutines.address

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.web.coroutine.function.client.CoroutineWebClient

@RunWith(MockitoJUnitRunner::class)
class AddressClientTests {

    private val objectMapper = jacksonObjectMapper()

    private val webClient: CoroutineWebClient = mock()
    private val uriSpec: CoroutineWebClient.RequestHeadersUriSpec<*> = mock()
    private val responseSpec: CoroutineWebClient.CoroutineResponseSpec = mock()

    private val addressClient = AddressClientImpl(webClient)

    private val address0 = Address(street = "BIRGER JARLSGATAN", state = "STOCKHOLM")
    private val address1 = Address(street = "BIRGER JARLSGATAN", state = "MALMÖ")

    private val singleAddressEnvelope = objectMapper.writeValueAsString(AddressEnvelope(api = AddressApi(), addresses = listOf(address0)))
    private val twoAddressesEnvelope = objectMapper.writeValueAsString(AddressEnvelope(api = AddressApi(), addresses = listOf(address0, address1)))

    @Before
    fun setUp() = runBlocking<Unit> {
        whenever(webClient.get()).thenReturn(uriSpec)
        whenever(uriSpec.uri(any(), any())).thenReturn(uriSpec)
        whenever(uriSpec.retrieve()).thenReturn(responseSpec)
    }

    @Test
    fun shouldGetSingleAddress() = runBlocking<Unit> {
        whenever(responseSpec.body<String>(any())).thenReturn(singleAddressEnvelope)

        val addresses = addressClient.getAddresses("")
        assertThat(addresses).hasSize(1).contains(address0)
    }

    @Test
    fun shouldGetTwoAddresses() = runBlocking<Unit> {
        whenever(responseSpec.body<String>(any())).thenReturn(twoAddressesEnvelope)

        val addresses = addressClient.getAddresses("")
        assertThat(addresses).hasSize(2).contains(address0).contains(address1)
    }
}
