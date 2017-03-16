package co.kenrg.yarnover.api.response

import co.kenrg.yarnover.api.domain.Paginator
import co.kenrg.yarnover.api.domain.VolumeDetails

data class PaginatedLibrarySearchResponse(
    val paginator: Paginator,
    val volumes: List<VolumeDetails>
)