package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class PatternAuthor(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "favorites_count") val favoritesCount: Int,
    @Json(name = "patterns_count") val patternsCount: Int,
    @Json(name = "users") val users: List<User>,
    @Json(name = "permalink") val permalink: String
)
