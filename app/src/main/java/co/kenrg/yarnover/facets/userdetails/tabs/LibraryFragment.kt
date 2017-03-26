package co.kenrg.yarnover.facets.userdetails.tabs

import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.oauth.UserManager

class LibraryFragment : BasePatternListFragment() {
  private val ravelryApi = api()

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
    else {
      val patternViewItems = response.body().volumes
          .filterNotNull()
          .map { (_, authorName, photo, _, patternId, _, _, _, _, title) ->
            val photoUrl = photo.mediumUrl ?: photo.medium2Url ?: photo.squareUrl
            ViewItem.Pattern(patternId, title, authorName, photoUrl)
          }
      onSuccess(patternViewItems)
    }
  }
}