package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class NeedleSize(
    @Json(name = "id") val id: Long,
    @Json(name = "hook") val hook: String?,
    @Json(name = "metric") val metric: Float?,
    @Json(name = "pretty_metric") val prettyMetric: String?,
    @Json(name = "us_steel") val usSteel: String?,
    @Json(name = "knitting") val isKnitting: Boolean,
    @Json(name = "name") val name: String,
    @Json(name = "crochet") val isCrochet: Boolean,
    @Json(name = "us") val usSize: String
)