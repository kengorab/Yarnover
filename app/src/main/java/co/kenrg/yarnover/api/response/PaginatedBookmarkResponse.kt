package co.kenrg.yarnover.api.response

import co.kenrg.yarnover.api.domain.Bookmark
import co.kenrg.yarnover.api.domain.Paginator

data class PaginatedBookmarkResponse(
    val paginator: Paginator,
    val favorites: List<Bookmark>
)