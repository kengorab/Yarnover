package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class Bookmark(
    @Json(name = "id") val id: Long,
    @Json(name = "comment") val comment: String,
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "favorited") val pattern: Pattern?,
    @Json(name = "tag_list") val tagList: String,
    @Json(name = "type") val type: String
)