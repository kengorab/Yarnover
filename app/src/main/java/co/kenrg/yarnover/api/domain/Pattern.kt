package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class Pattern(
    val id: Long,
    val name: String,
    val permalink: String,
    @Json(name = "first_photo") val firstPhoto: Photo,
    val designer: PatternAuthor,
    @Json(name = "pattern_author") val patternAuthor: PatternAuthor
)
