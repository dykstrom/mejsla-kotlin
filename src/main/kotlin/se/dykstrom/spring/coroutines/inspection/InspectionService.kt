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

import kotlinx.coroutines.experimental.async
import org.springframework.stereotype.Service
import se.dykstrom.spring.coroutines.address.AddressClient

/**
 * A service that provides the logic in managing inspection results.
 */
interface InspectionService {
    /**
     * Looks up inspection results using a REST service, and keeps those with remarks. The returned inspection
     * results will be decorated with their full address, looked up from a second REST service.
     */
    suspend fun getInspectionsWithRemarks(street: String): List<InspectionWithRemark>
}

@Service
class InspectionServiceImpl(
        private val addressClient: AddressClient,
        private val inspectionClient: InspectionClient
) : InspectionService {
    override suspend fun getInspectionsWithRemarks(street: String): List<InspectionWithRemark> {
        // Get all inspection records
        val inspections = inspectionClient.getInspections()
        println("${Thread.currentThread().name}: Read ${inspections.size} inspections")

        // Keep only those with remarks
        val inspectionsWithRemark = inspections.filter { it.hasRemark() }
        println("${Thread.currentThread().name}: Inspections with remark: ${inspectionsWithRemark.size}")

        // Keep only those on the requested street
        val inspectionsWithStreet = inspectionsWithRemark.filter { it.streetAddress.toLowerCase().startsWith(street.toLowerCase()) }
        println("${Thread.currentThread().name}: Inspections with requested street: ${inspectionsWithStreet.size}")

        // Load full addresses
        val inspectionsWithDeferredFullAddress = inspectionsWithStreet
                .map { Pair(it, async { addressClient.getAddresses(it.streetAddress) }) }
        println("${Thread.currentThread().name}: Inspections with deferred full address: ${inspectionsWithDeferredFullAddress.size}")

        val inspectionsWithFullAddress = inspectionsWithDeferredFullAddress
                .map { Pair(it.first, it.second.await()) }
        println("${Thread.currentThread().name}: Inspections with full address: ${inspectionsWithFullAddress.size}")

        // Keep only those with a Stockholm address
        val inspectionsWithStockholmAddress = inspectionsWithFullAddress
                .filter { it.second.any { it.isStockholm() } }
                .map { Pair(it.first, it.second.first { it.isStockholm() }) }
        println("${Thread.currentThread().name}: Inspections with Stockholm address: ${inspectionsWithStockholmAddress.size}")

        return inspectionsWithStockholmAddress
                .map {
                    InspectionWithRemark(
                            name = it.first.name,
                            streetAddress = it.first.streetAddress,
                            zipcode = it.second.zipcode,
                            city = it.second.city,
                            date = it.first.inspectionDate,
                            result = it.first.inspectionResult,
                            info = it.first.info
                    )
                }
    }
}
