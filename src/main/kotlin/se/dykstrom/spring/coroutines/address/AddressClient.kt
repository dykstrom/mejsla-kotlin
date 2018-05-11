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
import org.springframework.stereotype.Component
import org.springframework.web.coroutine.function.client.CoroutineWebClient

class AddressNotFoundException : RuntimeException()

/**
 * Client for the address lookup REST service.
 */
interface AddressClient {
    /**
     * Returns a list of full addresses matching the given `streetAddress`.
     * If no matching addresses can be found, this method returns an empty
     * list.
     */
    suspend fun getAddresses(streetAddress: String): List<Address>
}

@Component
class AddressClientImpl(private val webClient: CoroutineWebClient) : AddressClient {

    private val objectMapper = jacksonObjectMapper()

    private val url = "https://papapi.se/json/?s={s}&token={token}"
    //private val url = "http://localhost:8080/mock/addresses?s={s}"

    override suspend fun getAddresses(streetAddress: String): List<Address> {
        val params = mapOf("s" to streetAddress, "token" to "1a60abdfa969bbb84eb45d92f39d54f18655bb21")
        val body = webClient
                .get()
                .uri(url, params)
                .retrieve()
                .body(String::class.java)
        println("${Thread.currentThread().name}: $body")
        return if (body == null || "NOTHING FOUND" in body)
            emptyList()
        else
            objectMapper.readValue(body, AddressEnvelope::class.java).addresses
    }
}
