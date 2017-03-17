package co.kenrg.yarnover.api.response

import co.kenrg.yarnover.api.domain.Paginator
import co.kenrg.yarnover.api.domain.QueuedProjectDetails
import co.kenrg.yarnover.api.request.QueuedProject
import com.squareup.moshi.Json

data class PaginatedQueueResponse(
    val paginator: Paginator,
    @Json(name = "queued_projects") val queuedProjects: List<QueuedProjectDetails>
)