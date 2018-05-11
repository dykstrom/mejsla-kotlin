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

import com.fasterxml.jackson.annotation.JsonProperty

data class Inspection(
        var id: String = "",
        var timestamp: String = "",
        @JsonProperty("locationid") var locationId: String = "",
        @JsonProperty("namn") var name: String = "",
        @JsonProperty("objid") var objectId: String = "",
        @JsonProperty("uppdaterad") var updated: String = "",
        @JsonProperty("fastighet") var property: String = "",
        @JsonProperty("besadr") var streetAddress: String = "",
        @JsonProperty("verksamhetsid") var serviceId: String = "",
        @JsonProperty("reg") var register: String = "",
        @JsonProperty("verksamhetstyp") var serviceType: String = "",
        @JsonProperty("verksamhetstyp2") var serviceType2: String = "",
        @JsonProperty("inspdatum") var inspectionDate: String = "",
        @JsonProperty("inspbeslut") var inspectionResult: String = "",
        var info: String = "",
        @JsonProperty("geom") var geometry: String = ""
) {
    fun hasRemark() = inspectionResult != "" && !"Utan avvikelse".equals(inspectionResult, true)
}

data class InspectionEnvelope(
        var dataEntity: String = "",
        var resultRecords: String = "",
        var totalRecords: String = "",
        var srid: String = "",
        var records: List<Inspection> = listOf()
)

/**
 * Result type for the inspections endpoint.
 */
data class InspectionWithRemark(
        val name: String,
        val streetAddress: String,
        val zipcode: String,
        val city: String,
        val date: String,
        val result: String,
        val info: String
)
