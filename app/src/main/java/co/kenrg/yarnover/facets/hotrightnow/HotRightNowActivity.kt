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
import co.kenrg.yarnover.R
import co.kenrg.yarnover.ext.actionBarSize
import co.kenrg.yarnover.facets.hotrightnow.adapter.PatternDelegatorAdapter
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.oauth.OAuthManager
import co.kenrg.yarnover.oauth.SplashActivity
import org.jetbrains.anko.UI
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.wrapContent

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

//    requestPatterns()
    patternsAdapter.addPatterns(listOf(
        ViewItem.Pattern(1, "Pattern 1"),
        ViewItem.Pattern(2, "Pattern 2"),
        ViewItem.Pattern(3, "Pattern 3"),
        ViewItem.Pattern(4, "Pattern 4"),
        ViewItem.Pattern(5, "Pattern 5")
    ))

  }

//  fun requestPatterns() {
//    val retrofit = Retrofit.Builder()
//        .baseUrl("https://api.ravelry.com")
//        .addConverterFactory(MoshiConverterFactory.create())
//        .client(OAuthManager.getSignedClient())
//        .build()
//    val api = retrofit.create(RavelryApi::class.java)
//
//    doAsync {
//      val response = api.getCurrentUser().execute()
//
//      uiThread {
//        if (response.isSuccessful) {
//          userJsonField.text = response.body().user.username
//        } else {
//          userJsonField.text = response.errorBody().string()
//        }
//      }
//    }
//}

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
