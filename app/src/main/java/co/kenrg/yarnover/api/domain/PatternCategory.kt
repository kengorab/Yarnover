package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class PatternCategory(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "parent") val parent: PatternCategory?,
    @Json(name = "permalink") val permalink: String
)