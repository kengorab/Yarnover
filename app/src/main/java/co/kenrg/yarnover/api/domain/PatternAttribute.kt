package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class PatternAttribute(
    @Json(name = "permalink") val name: String,
    @Json(name = "id") val id: Long
)