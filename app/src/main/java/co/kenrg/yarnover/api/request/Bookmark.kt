package co.kenrg.yarnover.api.request

import com.squareup.moshi.Json

data class Bookmark(
    @Json(name = "favorited_id") val favoritedId: Long,
    @Json(name = "comment") val comment: String? = null,
    @Json(name = "tag_list") val tagList: String? = null,
    @Json(name = "type") val type: String = "pattern"
)