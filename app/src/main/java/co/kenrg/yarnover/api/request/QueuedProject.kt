package co.kenrg.yarnover.api.request

import com.squareup.moshi.Json

data class QueuedProject(
    @Json(name = "pattern_id") val patternId: Long
)