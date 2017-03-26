package co.kenrg.yarnover.facets.userdetails.tabs

import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.api.domain.VolumeDetails
import co.kenrg.yarnover.ui.patternlist.adapter.ViewItem
import co.kenrg.yarnover.oauth.UserManager
import co.kenrg.yarnover.ui.patternlist.BasePatternListFragment

class LibraryFragment : BasePatternListFragment() {
  private val ravelryApi = api()

  private fun getViewItems(libraryPatterns: List<VolumeDetails>) =
      libraryPatterns.map { (_, authorName, photo, _, patternId, _, _, _, _, title) ->
        val photoUrl = photo.mediumUrl ?: photo.medium2Url ?: photo.squareUrl
        ViewItem.Pattern(patternId, title, authorName, photoUrl)
      }

  override fun getPatterns(
      page: Int,
      onSuccess: (List<ViewItem.Pattern>) -> Unit,
      onFailure: (String) -> Unit
  ) {
    val response = ravelryApi.getLibrary(
        username = UserManager.getUsername(),
        page = page,
        pageSize = maxPerPage
    ).execute()

    if (!response.isSuccessful) onFailure("There was a problem fetching library patterns")
    else onSuccess(getViewItems(response.body().volumes))
  }
}