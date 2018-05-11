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
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.springframework.stereotype.Component
import org.springframework.web.coroutine.function.client.CoroutineWebClient

class InspectionNotFoundException : RuntimeException()

/**
 * Client for the inspection lookup REST service.
 */
interface InspectionClient {
    /**
     * Returns the list of all available inspection results.
     */
    suspend fun getInspections(): List<Inspection>
}

@Component
class InspectionClientImpl(
        private val webClient: CoroutineWebClient,
        private val batchSize: Int = 1000
) : InspectionClient {

    private val objectMapper = jacksonObjectMapper()

    private val baseUrl = "http://miljodata.stockholm.se/api/ecos-2016-verksamheterlivs"
    //private val baseUrl = "http://localhost:8080/mock/inspections"
    private val infoUrl = "$baseUrl?\$format=json&\$info"
    private val getUrl = "$baseUrl?\$format=json&\$limit={limit}&\$offset={offset}"

    override suspend fun getInspections(): List<Inspection> {
        val totalRecords = getTotalRecords()

        val batches = ArrayList<Deferred<List<Inspection>>>()
        for (offset in 0 until totalRecords step batchSize) {
            batches += async { getInspectionsWithOffset(offset) }
        }
        return batches.flatMap { it.await() }.toList()
    }

    /**
     * Returns total number of inspection records.
     */
    private suspend fun getTotalRecords(): Int {
        val body = webClient
                .get()
                .uri(infoUrl)
                .retrieve()
                .body(String::class.java)
        return parse(body).totalRecords.toInt()
    }

    /**
     * Returns inspection records, starting from the given `offset`.
     */
    private suspend fun getInspectionsWithOffset(offset: Int): List<Inspection> {
        val body = webClient.get()
                .uri(getUrl, mapOf("limit" to batchSize, "offset" to offset.toString()))
                .retrieve()
                .body(String::class.java)
        return parse(body).records
    }

    private fun parse(body: String?): InspectionEnvelope {
        if (body == null)
            throw InspectionNotFoundException()
        else
            return objectMapper.readValue(body, InspectionEnvelope::class.java)
    }
}
