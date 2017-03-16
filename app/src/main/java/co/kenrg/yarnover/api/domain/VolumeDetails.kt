package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class VolumeDetails(
    @Json(name = "id") val id: Long,
    @Json(name = "author_name") val author_name: String,
    //    @Json(name = "cover_image_size") val cover_image_size:,
    //    @Json(name = "cover_image_url") val cover_image_url:,
    @Json(name = "first_photo") val firstPhoto: Photo,
    @Json(name = "has_downloads") val hasDownloads: Boolean,
    @Json(name = "pattern_id") val patternId: Long,
    @Json(name = "pattern_source_id") val patternSourceId: Int?,
    @Json(name = "patterns_count") val patternsCount: Int,
    @Json(name = "small_image_url") val smallImageUrl: String,
    @Json(name = "square_image_url") val squareImageUrl: String,
    @Json(name = "title") val title: String
)