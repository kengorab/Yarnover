package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class PatternSource(
    @Json(name = "id") val id: Long,
    @Json(name = "author") val author: String,
    @Json(name = "name") val name: String,
    @Json(name = "list_price") val listPrice: Float?,
    @Json(name = "patterns_count") val numPatterns: Int,
    @Json(name = "out_of_print") val isOutOfPrint: Boolean,
    @Json(name = "price") val price: Float?,
    @Json(name = "permalink") val permalink: String,
    @Json(name = "url") val url: String,
    @Json(name = "amazon_url") val amazonUrl: String?,
    @Json(name = "amazon_rating") val amazonRating: Float?,
    @Json(name = "shelf_image_path") val shelfImagePath: String?
)