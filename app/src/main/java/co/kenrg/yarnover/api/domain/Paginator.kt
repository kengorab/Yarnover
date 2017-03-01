package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class Paginator(
    @Json(name = "page_count") val pageCount: Int,
    @Json(name = "results") val results: Int,
    @Json(name = "last_page") val lastPage: Int,
    @Json(name = "page") val page: Int,
    @Json(name = "page_size") val pageSize: Int
)
