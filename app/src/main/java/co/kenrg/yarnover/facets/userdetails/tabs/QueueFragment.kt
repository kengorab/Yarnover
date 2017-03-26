package co.kenrg.yarnover.facets.userdetails.tabs

import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.api.domain.QueuedProjectDetails
import co.kenrg.yarnover.ui.patternlist.adapter.ViewItem
import co.kenrg.yarnover.oauth.UserManager
import co.kenrg.yarnover.ui.patternlist.BasePatternListFragment

class QueueFragment : BasePatternListFragment() {
  private val ravelryApi = api()

  private fun getViewItems(queuedProjects: List<QueuedProjectDetails>) =
      queuedProjects.map { (_, photo, _, _, _, patternId, patternName, patternAuthorName) ->
        val photoUrl = photo.mediumUrl ?: photo.medium2Url ?: photo.squareUrl
        val authorName = patternAuthorName ?: ""
        ViewItem.Pattern(patternId, patternName, authorName, photoUrl)
      }

  override fun getPatterns(
      page: Int,
      onSuccess: (List<ViewItem.Pattern>) -> Unit,
      onFailure: (String) -> Unit
  ) {
    val response = ravelryApi.getQueue(
        username = UserManager.getUsername(),
        page = page,
        pageSize = maxPerPage
    ).execute()

    if (!response.isSuccessful) onFailure("There was a problem fetching queued patterns")
    else onSuccess(getViewItems(response.body().queuedProjects))
  }
}