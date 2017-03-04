package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class YarnWeight(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "crochet_gauge") val crochetGauge: String,
    @Json(name = "knit_gauge") val knitGauge: String,
    @Json(name = "max_gauge") val maxGauge: String,
    @Json(name = "min_gauge") val minGauge: String,
    @Json(name = "ply") val ply: String,
    @Json(name = "wpi") val wpi: String
)