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

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import se.dykstrom.spring.coroutines.address.Address
import se.dykstrom.spring.coroutines.address.AddressClient

@RunWith(MockitoJUnitRunner::class)
class InspectionServiceTests {

    private val addressClient: AddressClient = mock()
    private val inspectionClient: InspectionClient = mock()

    private val service = InspectionServiceImpl(addressClient, inspectionClient)

    private val address0 = Address(street = "BIRGER JARLSGATAN", zipcode = "114 34", state = "STOCKHOLM")
    private val address2 = Address(street = "BIRGER JARLSGATAN", zipcode = "114 35", state = "STOCKHOLM")

    private val streetAddress0 = "Birger Jarlsgatan 10"
    private val streetAddress1 = "Birger Jarlsgatan 11"
    private val streetAddress2 = "Birger Jarlsgatan 12"
    private val streetAddress3 = "Klacktorget 4"

    private val inspection0 = Inspection(name = "Bengans Burgare", streetAddress = streetAddress0, inspectionDate = "2018-04-01", inspectionResult = "Med avvikelse")
    private val inspection1 = Inspection(name = "Kalles Korv", streetAddress = streetAddress1, inspectionDate = "2018-04-01", inspectionResult = "")
    private val inspection2 = Inspection(name = "Fredriks Fisk", streetAddress = streetAddress2, inspectionDate = "2018-04-01", inspectionResult = "Med avvikelse")
    private val inspection3 = Inspection(name = "Kristers Kebab", streetAddress = streetAddress3, inspectionDate = "2018-04-01", inspectionResult = "Med avvikelse")

    @Before
    fun setUp() = runBlocking<Unit> {
        // Set up common expectations to handle coroutine stuff
        whenever(addressClient.getAddresses(streetAddress0)).thenReturn(listOf(address0))
        whenever(addressClient.getAddresses(streetAddress2)).thenReturn(listOf(address2))
    }

    @Test
    fun shouldGetOneInspectionOfOnePossible() = runBlocking<Unit> {
        whenever(inspectionClient.getInspections()).thenReturn(listOf(inspection0))

        val inspectionsWithRemarks = service.getInspectionsWithRemarks("Birger Jarlsgatan")

        assertThat(inspectionsWithRemarks).hasSize(1).anyMatch { it.zipcode == address0.zipcode }
    }

    @Test
    fun shouldGetOneInspectionOfTwoPossible() = runBlocking<Unit> {
        whenever(inspectionClient.getInspections()).thenReturn(listOf(inspection0, inspection1))

        val inspectionsWithRemarks = service.getInspectionsWithRemarks("Birger Jarlsgatan")

        assertThat(inspectionsWithRemarks).hasSize(1).anyMatch { it.zipcode == address0.zipcode }
    }

    @Test
    fun shouldGetTwoInspectionsOfThreePossible() = runBlocking<Unit> {
        whenever(inspectionClient.getInspections()).thenReturn(listOf(inspection0, inspection1, inspection2))

        val inspectionsWithRemarks = service.getInspectionsWithRemarks("Birger Jarlsgatan")

        assertThat(inspectionsWithRemarks).hasSize(2).anyMatch { it.zipcode == address0.zipcode }.anyMatch { it.zipcode == address2.zipcode }
    }

    @Test
    fun shouldGetTwoInspectionsOfThreePossibleFilterByStreet() = runBlocking<Unit> {
        whenever(inspectionClient.getInspections()).thenReturn(listOf(inspection0, inspection2, inspection3))

        val start = System.currentTimeMillis()
        val inspectionsWithRemarks = service.getInspectionsWithRemarks("Birger Jarlsgatan")
        val stop = System.currentTimeMillis()
        println("Time: ${stop - start} millis")

        assertThat(inspectionsWithRemarks).hasSize(2).anyMatch { it.zipcode == address0.zipcode }.anyMatch { it.zipcode == address2.zipcode }
    }

    @Test
    fun shouldGetNoInspectionsOfOnePossible() = runBlocking {
        whenever(inspectionClient.getInspections()).thenReturn(listOf(inspection1))

        val inspectionsWithRemarks = service.getInspectionsWithRemarks("Birger Jarlsgatan")

        assertThat(inspectionsWithRemarks).isEmpty()
    }
}
