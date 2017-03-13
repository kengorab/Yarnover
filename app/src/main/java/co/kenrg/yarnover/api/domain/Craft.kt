package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class Craft(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "permalink") val permalink: String
)