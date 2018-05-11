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

package se.dykstrom.spring.coroutines.mock

import org.springframework.http.ResponseEntity
import org.springframework.kotlin.experimental.coroutine.EnableCoroutine
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import se.dykstrom.spring.coroutines.address.Address
import se.dykstrom.spring.coroutines.address.AddressApi
import se.dykstrom.spring.coroutines.address.AddressEnvelope
import se.dykstrom.spring.coroutines.address.AddressNotFoundException
import se.dykstrom.spring.coroutines.inspection.BaseExceptionHandler
import se.dykstrom.spring.coroutines.inspection.Inspection
import se.dykstrom.spring.coroutines.inspection.InspectionEnvelope
import java.util.*
import java.util.regex.Pattern

@RestController
@EnableCoroutine
class MockController : BaseExceptionHandler {

    private val streetAddressPattern = Pattern.compile("^(.*) ([0-9]+)$")!!
    private val random = Random()

    @GetMapping("/mock/addresses")
    suspend fun getAddresses(@RequestParam s: String): ResponseEntity<AddressEnvelope> {
        val matcher = streetAddressPattern.matcher(s)
        if (matcher.matches()) {
            val street = matcher.group(1)
            val number = matcher.group(2)
            val zipcode = random.nextInt(10000) + 10000
            val addresses = listOf(
                    Address(street = street.toUpperCase(), number = number, zipcode = zipcode.toString(), city = "STOCKHOLM", state = "STOCKHOLM"),
                    Address(street = street.toUpperCase(), number = number, zipcode = "65225", city = "KARLSTAD", state = "KARLSTAD")
            )
            return ResponseEntity.ok(AddressEnvelope(api = AddressApi(name = "MOCK-API"), addresses = addresses))
        } else {
            throw AddressNotFoundException()
        }
    }

    @GetMapping("/mock/inspections")
    suspend fun getInspections(): InspectionEnvelope {
        val inspection0 = Inspection(name = "Food Service 0", streetAddress = "Birger Jarlsgatan 10", inspectionResult = "Med avvikelse")
        val inspection1 = Inspection(name = "Food Service 1", streetAddress = "Birger Jarlsgatan 12", inspectionResult = "Utan avvikelse")
        val inspection2 = Inspection(name = "Food Service 2", streetAddress = "Birger Jarlsgatan 14", inspectionResult = "Med avvikelse")
        val inspection3 = Inspection(name = "Food Service 3", streetAddress = "Kungsgatan 5", inspectionResult = "Med avvikelse")
        val records = listOf(inspection0, inspection1, inspection2, inspection3)
        return InspectionEnvelope(resultRecords = records.size.toString(), totalRecords = records.size.toString(), records = records)
    }
}
