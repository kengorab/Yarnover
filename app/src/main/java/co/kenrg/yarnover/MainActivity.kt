package co.kenrg.yarnover

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem.SHOW_AS_ACTION_NEVER
import android.view.View
import android.widget.TextView
import co.kenrg.yarnover.api.RavelryApi
import co.kenrg.yarnover.ext.actionBarSize
import co.kenrg.yarnover.oauth.OAuthManager
import co.kenrg.yarnover.oauth.SplashActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {
  private lateinit var userJsonField: TextView

  private fun makeLayout(mainActivity: MainActivity): View =
      mainActivity.UI {
        coordinatorLayout {
          fitsSystemWindows = true

          appBarLayout {
            lparams(width = matchParent, height = wrapContent)

            toolbar(R.style.AppTheme_PopupOverlay) {
              mainActivity.setSupportActionBar(this)
              mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }.lparams(width = matchParent, height = actionBarSize()) {
              scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS
            }
          }

          frameLayout {
            userJsonField = textView()
          }.lparams(width = matchParent, height = matchParent) {
            behavior = AppBarLayout.ScrollingViewBehavior()
          }
        }
      }.view

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(makeLayout(this))


    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.ravelry.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(OAuthManager.getSignedClient())
        .build()
    val api = retrofit.create(RavelryApi::class.java)

    doAsync {
      val response = api.getCurrentUser().execute()

      uiThread {
        if (response.isSuccessful) {
          userJsonField.text = response.body().user.username
        } else {
          userJsonField.text = response.errorBody().string()
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
