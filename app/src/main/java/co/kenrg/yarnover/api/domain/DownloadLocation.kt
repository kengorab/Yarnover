package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class DownloadLocation(
    @Json(name = "activated_at") val activatedAt: String,
    @Json(name = "expires_at") val expiresAt: String,
    @Json(name = "url") val url: String
)