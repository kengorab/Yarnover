package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class Photo(
    @Json(name = "id") val id: Long,
    @Json(name = "x_offset") val xOffset: Int,
    @Json(name = "y_offset") val yOffset: Int,
    @Json(name = "sort_order") val sortOrder: Int,
    @Json(name = "thumbnail_url") val thumbnailUrl: String,
    @Json(name = "square_url") val squareUrl: String,
    @Json(name = "small_url") val smallUrl: String,
    @Json(name = "small2_url") val small2Url: String,
    @Json(name = "medium_url") val mediumUrl: String,
    @Json(name = "medium2_url") val medium2Url: String
)
