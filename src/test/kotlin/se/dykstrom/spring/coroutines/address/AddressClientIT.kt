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

import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.web.coroutine.function.client.CoroutineWebClient

class AddressClientIT {

    private val addressClient = AddressClientImpl(CoroutineWebClient.create())

    @Test
    fun shouldGetAddresses() = runBlocking<Unit> {
        val addresses = addressClient.getAddresses("Birger Jarlsgatan 10")
        assertThat(addresses).anyMatch { it.zipcode.startsWith("1") }
    }

    @Test
    fun shouldNotGetAddress() = runBlocking {
        val addresses = addressClient.getAddresses("Does not exist")
        assertThat(addresses).isEmpty()
    }
}
