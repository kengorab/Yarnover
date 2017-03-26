package co.kenrg.yarnover.facets.userdetails.tabs

import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.api.domain.Pattern
import co.kenrg.yarnover.ui.patternlist.adapter.ViewItem
import co.kenrg.yarnover.ui.patternlist.BasePatternListFragment

class HotRightNowPatternsFragment : BasePatternListFragment() {
  private val ravelryApi = api()

  private fun getViewItems(patterns: List<Pattern>) =
      patterns.map { (id, name, _, firstPhoto, designer) ->
        val photoUrl = firstPhoto.mediumUrl ?: firstPhoto.medium2Url ?: firstPhoto.squareUrl
        ViewItem.Pattern(id, name, designer.name, photoUrl)
      }

  override fun getPatterns(
      page: Int,
      onSuccess: (List<ViewItem.Pattern>) -> Unit,
      onFailure: (String) -> Unit
  ) {
    val response = ravelryApi.searchPatterns(page = page).execute()

    if (!response.isSuccessful) onFailure("There was a problem fetching patterns")
    else onSuccess(getViewItems(response.body().patterns))
  }
}