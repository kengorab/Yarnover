package co.kenrg.yarnover.facets.hotrightnow

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem.SHOW_AS_ACTION_NEVER
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import co.kenrg.yarnover.R
import co.kenrg.yarnover.api.RavelryApi
import co.kenrg.yarnover.ext.actionBarSize
import co.kenrg.yarnover.facets.hotrightnow.adapter.PatternDelegatorAdapter
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.oauth.OAuthManager
import co.kenrg.yarnover.oauth.SplashActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class HotRightNowActivity : AppCompatActivity() {
  lateinit private var patternList: RecyclerView
  private val patternsAdapter = PatternDelegatorAdapter()

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
            patternList = recyclerView {
              lparams(width = matchParent, height = matchParent)

              adapter = patternsAdapter
              setHasFixedSize(true)

              val linearLayout = LinearLayoutManager(context)
              layoutManager = linearLayout
            }
          }.lparams(width = matchParent, height = matchParent) {
            behavior = AppBarLayout.ScrollingViewBehavior()
          }
        }
      }.view

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(makeLayout(this))

    requestPatterns()
  }

  fun requestPatterns() {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.ravelry.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(OAuthManager.getSignedClient())
        .build()
    val api = retrofit.create(RavelryApi::class.java)

    doAsync {
      val response = api.searchPatterns(1, 1).execute()

      uiThread {
        if (!response.isSuccessful) {
          Toast.makeText(this@HotRightNowActivity, "Error fetching patterns...", LENGTH_LONG).show()
        } else {
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
}
