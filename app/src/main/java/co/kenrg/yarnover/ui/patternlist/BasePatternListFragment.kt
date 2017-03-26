package co.kenrg.yarnover.ui.patternlist

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
import co.kenrg.yarnover.facets.patterndetails.PatternDetailsActivity
import co.kenrg.yarnover.facets.patterndetails.PatternDetailsActivity.Companion.KEY_PATTERN_DATA
import co.kenrg.yarnover.ui.patternlist.adapter.PatternDelegatorAdapter
import co.kenrg.yarnover.ui.patternlist.adapter.PatternListParcel
import co.kenrg.yarnover.ui.patternlist.adapter.ViewItem
import kotlinx.android.synthetic.main.component_patterncard.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

abstract class BasePatternListFragment : Fragment() {
  companion object {
    private val KEY_PARCEL = "PARCEL"
    val maxPerPage = 25
  }

  private val patternsAdapter = PatternDelegatorAdapter(onPatternClick = { item, view ->
    handleSelectPattern(item, view)
  })

  private lateinit var patternList: RecyclerView
  private var currentPage = 0

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    val patterns = patternsAdapter.getPatterns()
    if (patterns.isNotEmpty())
      outState?.putParcelable(KEY_PARCEL, PatternListParcel(currentPage, patterns))
  }

  abstract fun getPatterns(
      page: Int,
      onSuccess: (List<ViewItem.Pattern>) -> Unit,
      onFailure: (String) -> Unit
  )

  private fun loadPatterns(page: Int) {
    doAsync {
      getPatterns(
          page = page,
          onSuccess = { patternViewItems ->
            uiThread {
              currentPage = page
              patternsAdapter.addPatterns(patternViewItems, patternViewItems.size >= maxPerPage)
            }
          },
          onFailure = { message ->
            uiThread {
              Snackbar.make(patternList, message, Snackbar.LENGTH_LONG)
                  .setAction("Retry") { loadPatterns(page) }
                  .setDuration(3000)
                  .show()
            }
          }
      )
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    patternList = RecyclerView(activity).apply {
      layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

      this.adapter = patternsAdapter

      val linearLayoutManager = LinearLayoutManager(context)
      this.layoutManager = linearLayoutManager

      this.clearOnScrollListeners()
      this.addOnScrollListener(co.kenrg.yarnover.iface.adapter.InfiniteScrollListener(linearLayoutManager) {
        loadPatterns(currentPage + 1)
      })
    }

    if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PARCEL)) {
      // Load data from saved state (i.e. orientation change)

      val parcel = savedInstanceState.get(KEY_PARCEL) as PatternListParcel
      currentPage = parcel.currentPage
      patternsAdapter.replaceWithPatterns(parcel.patterns, parcel.patterns.size >= maxPerPage)
    } else if (currentPage == 0) {
      // Load fresh data

      loadPatterns(currentPage + 1)
    } else {
      // Nothing to do, data already present in adapter
    }

    return patternList
  }

  private fun handleSelectPattern(pattern: ViewItem.Pattern, view: View) {
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
