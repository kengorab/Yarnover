package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class Bookmark(
    @Json(name = "id") val id: Long
)