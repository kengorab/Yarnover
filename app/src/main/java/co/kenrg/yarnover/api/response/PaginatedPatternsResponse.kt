package co.kenrg.yarnover.api.response

import co.kenrg.yarnover.api.domain.Paginator
import co.kenrg.yarnover.api.domain.Pattern

data class PaginatedPatternsResponse(
    val paginator: Paginator,
    val patterns: List<Pattern>
)