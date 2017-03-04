package co.kenrg.yarnover.facets.hotrightnow

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem.SHOW_AS_ACTION_NEVER
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import co.kenrg.yarnover.R
import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.facets.hotrightnow.adapter.PatternDelegatorAdapter
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.iface.adapter.InfiniteScrollListener
import co.kenrg.yarnover.oauth.OAuthManager
import co.kenrg.yarnover.oauth.SplashActivity
import kotlinx.android.synthetic.main.activity_hotrightnow.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class HotRightNowActivity : AppCompatActivity() {
  private val activity: HotRightNowActivity = this
  private val parcelKey = "HOT_RIGHT_NOW_ACTIVITY_PARCEL"
  private val ravelryApi = api()
  private val patternsAdapter = PatternDelegatorAdapter({ this.handleSelectPattern(it) })

  private var currentPage = 0

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    val patterns = patternsAdapter.getPatterns()
    if (patterns.isNotEmpty())
      outState?.putParcelable(parcelKey, HotRightNowParcel(currentPage, patterns))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_hotrightnow)

    toolbar.apply {
      activity.setSupportActionBar(this)
      activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    patternList.apply {
      this.adapter = patternsAdapter

      val linearLayoutManager = LinearLayoutManager(context)
      this.layoutManager = linearLayoutManager

      this.clearOnScrollListeners()
      this.addOnScrollListener(InfiniteScrollListener(linearLayoutManager) {
        requestPatterns(currentPage + 1)
      })
    }

    if (savedInstanceState != null && savedInstanceState.containsKey(parcelKey)) {
      val parcel = savedInstanceState.get(parcelKey) as HotRightNowParcel
      currentPage = parcel.currentPage
      patternsAdapter.replaceWithPatterns(parcel.patterns)
    } else {
      requestPatterns(currentPage + 1)
    }
  }

  fun requestPatterns(page: Int) {
    doAsync {
      val response = ravelryApi.searchPatterns(
          page = page
      ).execute()

      uiThread {
        if (!response.isSuccessful) {
          // TODO - Replace this with snackbar, prompting user to retry request
          Toast.makeText(this@HotRightNowActivity, "Error fetching patterns...", LENGTH_LONG).show()
        } else {
          currentPage = page
          val patternViewItems = response.body().patterns
              .map { (id, name, _, firstPhoto, designer) ->
                ViewItem.Pattern(id, name, designer.name, firstPhoto.squareUrl)
              }
          patternsAdapter.addPatterns(patternViewItems)
        }
      }
    }
  }

  fun handleSelectPattern(pattern: ViewItem.Pattern) {
    Toast.makeText(this, "Selected ${pattern.patternName}", LENGTH_LONG).show()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val logoutMenuItem = menu?.add("Logout")
    logoutMenuItem?.setShowAsAction(SHOW_AS_ACTION_NEVER)
    logoutMenuItem?.setOnMenuItemClickListener { handleLogout() }
    return true
  }

  fun handleLogout(): Boolean {
    OAuthManager.clearAccessToken(this)
    startActivity(Intent(this, SplashActivity::class.java))
    finish()
    return true
  }
}
