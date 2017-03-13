package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class Printing(
    @Json(name = "primary_source") val isPrimarySource: Boolean,
    @Json(name = "pattern_source") val source: PatternSource
)