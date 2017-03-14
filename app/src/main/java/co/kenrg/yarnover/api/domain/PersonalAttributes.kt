package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class PersonalAttributes(
    @Json(name = "bookmark_id") val bookmarkId: Long?,
    @Json(name = "in_library") val isInLibrary: Boolean,
    @Json(name = "queued") val isQueued: Boolean,
    @Json(name = "favorited") val isFavorite: Boolean
)