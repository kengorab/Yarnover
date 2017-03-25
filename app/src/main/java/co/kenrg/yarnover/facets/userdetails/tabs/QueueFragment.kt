package co.kenrg.yarnover.facets.userdetails.tabs

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import co.kenrg.yarnover.R
import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.facets.hotrightnow.HotRightNowParcel
import co.kenrg.yarnover.facets.hotrightnow.adapter.PatternDelegatorAdapter
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.facets.patterndetails.PatternDetailsActivity
import co.kenrg.yarnover.facets.patterndetails.PatternDetailsActivity.Companion.KEY_PATTERN_DATA
import co.kenrg.yarnover.iface.adapter.InfiniteScrollListener
import co.kenrg.yarnover.oauth.UserManager
import kotlinx.android.synthetic.main.component_patterncard.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class QueueFragment : Fragment() {
  companion object {
    private val KEY_PARCEL = "QUEUE_PARCEL"
    private val maxPerPage = 25
  }

  private val ravelryApi = api()

  // TODO - Move PatternDelegatorAdaptor from the HRN facets package
  private val patternsAdapter = PatternDelegatorAdapter(onPatternClick = { item, view ->
    handleSelectPattern(item, view)
  })

  private lateinit var queueList: RecyclerView
  private var currentPage = 0

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    val patterns = patternsAdapter.getPatterns()
    if (patterns.isNotEmpty())
      outState?.putParcelable(KEY_PARCEL, HotRightNowParcel(currentPage, patterns))
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    queueList = RecyclerView(activity).apply {
      layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

      this.adapter = patternsAdapter

      val linearLayoutManager = LinearLayoutManager(context)
      this.layoutManager = linearLayoutManager

      this.clearOnScrollListeners()
      this.addOnScrollListener(InfiniteScrollListener(linearLayoutManager) {
        requestQueuedPatterns(currentPage + 1)
      })
    }

    if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PARCEL)) {
      val parcel = savedInstanceState.get(KEY_PARCEL) as HotRightNowParcel
      currentPage = parcel.currentPage
      patternsAdapter.replaceWithPatterns(parcel.patterns, parcel.patterns.size >= maxPerPage)
    } else {
      requestQueuedPatterns(currentPage + 1)
    }

    return queueList
  }

  fun requestQueuedPatterns(page: Int) {
    doAsync {
      val response = ravelryApi.getQueue(
          username = UserManager.getUsername(),
          page = page,
          pageSize = maxPerPage
      ).execute()

      uiThread {
        if (!response.isSuccessful) {
          Snackbar.make(queueList, "There was a problem fetching queued patterns", Snackbar.LENGTH_LONG)
              .setAction("Retry") { requestQueuedPatterns(page) }
              .setDuration(3000)
              .show()
        } else {
          currentPage = page

          val patternViewItems = response.body().queuedProjects
              .filterNotNull()
              .map { (_, photo, _, _, _, patternId, patternName, patternAuthorName) ->
                val photoUrl = photo.mediumUrl ?: photo.medium2Url ?: photo.squareUrl
                val authorName = patternAuthorName ?: ""
                ViewItem.Pattern(patternId, patternName, authorName, photoUrl)
              }
          patternsAdapter.addPatterns(patternViewItems, patternViewItems.size >= maxPerPage)
        }
      }
    }
  }

  fun handleSelectPattern(pattern: ViewItem.Pattern, view: View) {
    val intent = Intent(activity, PatternDetailsActivity::class.java)
    intent.putExtra(KEY_PATTERN_DATA, pattern)

    val bundle = getSharedElementTransitionBundle(view)
    if (bundle != null)
      startActivity(intent, bundle)
    else
      startActivity(intent)
  }

  private fun getSharedElementTransitionBundle(patternView: View): Bundle? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val animation = ActivityOptions.makeSceneTransitionAnimation(activity, Pair.create(
          patternView.previewImage as View,
          getString(R.string.transition_preview_image)
      ))
      return animation.toBundle()
    } else {
      return null
    }
  }
}