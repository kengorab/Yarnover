package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class CurrentUser(
    val id: Long,
    val username: String,
    @Json(name = "small_photo_url") val smallPhotoUrl: String,
    @Json(name = "tiny_photo_url") val tinyPhotoUrl: String,
    @Json(name = "large_photo_url") val largePhotoUrl: String,
    @Json(name = "photo_url") val photoUrl: String
)
