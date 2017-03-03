package co.kenrg.yarnover.facets.hotrightnow

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem.SHOW_AS_ACTION_NEVER
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import co.kenrg.yarnover.R
import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.api.domain.Pattern
import co.kenrg.yarnover.ext.actionBarSize
import co.kenrg.yarnover.facets.hotrightnow.adapter.PatternDelegatorAdapter
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.iface.adapter.InfiniteScrollListener
import co.kenrg.yarnover.oauth.OAuthManager
import co.kenrg.yarnover.oauth.SplashActivity
import org.jetbrains.anko.UI
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.uiThread
import org.jetbrains.anko.wrapContent

class HotRightNowActivity : AppCompatActivity() {
  private val ravelryApi = api()
  private val patternsAdapter = PatternDelegatorAdapter({ this.handleSelectPattern(it) })

  lateinit private var patternList: RecyclerView
  private var currentPage = 0

  private fun makeLayout(activity: HotRightNowActivity): View =
      activity.UI {
        coordinatorLayout {
          fitsSystemWindows = true

          appBarLayout {
            lparams(width = matchParent, height = wrapContent)

            toolbar(R.style.AppTheme_PopupOverlay) {
              activity.setSupportActionBar(this)
              activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }.lparams(width = matchParent, height = actionBarSize()) {
              scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS
            }
          }

          frameLayout {
            backgroundColor = getColor(activity, R.color.backgroundColor)

            patternList = recyclerView {
              lparams(width = matchParent, height = matchParent)

              adapter = patternsAdapter
              setHasFixedSize(true)

              val linearLayout = LinearLayoutManager(context)
              layoutManager = linearLayout

              clearOnScrollListeners()
              addOnScrollListener(InfiniteScrollListener(linearLayout) {
                requestPatterns(currentPage + 1)
              })
            }
          }.lparams(width = matchParent, height = matchParent) {
            behavior = AppBarLayout.ScrollingViewBehavior()
          }
        }
      }.view

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(makeLayout(this))

    requestPatterns(currentPage + 1)
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
          val patternViewItems = response.body().patterns.map { pattern ->
            ViewItem.Pattern(pattern)
          }
          patternsAdapter.addPatterns(patternViewItems)
        }
      }
    }
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

  fun handleSelectPattern(pattern: Pattern) {
    Toast.makeText(this, "Selected ${pattern.name}", LENGTH_LONG).show()
  }
}
