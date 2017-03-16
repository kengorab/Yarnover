package co.kenrg.yarnover.api.query

import com.squareup.moshi.Json

data class Volume(
    @Json(name = "pattern_id") val patternId: Long,
    @Json(name = "asking_price") val askingPrice: String? = null,
    @Json(name = "asking_price_currency") val askingPriceCurrency: String? = null,
    @Json(name = "location") val location: String? = null,
    @Json(name = "pattern_source_id") val patternSourceId: Int? = null,
    @Json(name = "volume_status_id") val volumeStatusId: Int? = null
)