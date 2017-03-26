package co.kenrg.yarnover.facets.userdetails.tabs

import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.api.domain.Bookmark
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.oauth.UserManager

class FavoritesFragment : BasePatternListFragment() {
  private val ravelryApi = api()

  private fun getViewItems(bookmarks: List<Bookmark>) =
      bookmarks
          .map(Bookmark::pattern)
          .filterNotNull()
          .map { (id, name, _, firstPhoto, designer) ->
            val photoUrl = firstPhoto.mediumUrl ?: firstPhoto.medium2Url ?: firstPhoto.squareUrl
            ViewItem.Pattern(id, name, designer.name, photoUrl)
          }

  override fun getPatterns(
      page: Int,
      onSuccess: (List<ViewItem.Pattern>) -> Unit,
      onFailure: (String) -> Unit
  ) {
    val response = ravelryApi.getFavorites(
        username = UserManager.getUsername(),
        page = page,
        pageSize = maxPerPage
    ).execute()

    if (!response.isSuccessful) onFailure("There was a problem fetching favorited patterns")
    else onSuccess(getViewItems(response.body().favorites))
  }
}