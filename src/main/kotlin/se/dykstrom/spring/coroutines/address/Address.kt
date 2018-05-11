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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Address(
        var street: String = "",
        var number: String = "",
        var zipcode: String = "",
        var city: String = "",
        var municipality: String = "",
        var code: String = "",
        var state: String = ""
) {
    @JsonIgnore fun isStockholm() = "STOCKHOLM".equals(state, true)
}

data class AddressApi(
        var name: String = "",
        var url: String = "",
        var version: String = "",
        var encoding: String = ""
)

data class AddressEnvelope(
        var api: AddressApi,
        @JsonProperty("result") var addresses: List<Address> = listOf()
)
