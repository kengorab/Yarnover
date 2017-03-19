package co.kenrg.yarnover.oauth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.kenrg.yarnover.R
import co.kenrg.yarnover.facets.hotrightnow.HotRightNowActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SplashActivity : AppCompatActivity() {
  private val activity: SplashActivity = this

  companion object {
    fun startMainActivity(rootActivity: AppCompatActivity) {
      fun startActivity() {
        val intent = Intent(rootActivity, HotRightNowActivity::class.java)
        rootActivity.startActivity(intent)
        rootActivity.finish()
      }

      if (!UserManager.isUserInSharedPrefs())
        UserManager.storeUsernameInSharedPrefs { startActivity() }
      else startActivity()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (OAuthManager.setAccessTokenFromSharedPrefs()) {
      startMainActivity(this)
    } else {
      setContentView(R.layout.activity_splash)
      loginToRavelry.setOnClickListener {
        getNewAccessToken()
      }
    }
  }

  fun getNewAccessToken() {
    doAsync {
      val authorizeAppUrl = OAuthManager.getAuthorizeAppUrl()
      val uri = Uri.parse(authorizeAppUrl)

      uiThread {
        if (uri != null) {
          val browser = Intent(activity, OAuthActivity::class.java)
          browser.data = uri

          activity.startActivity(browser)
          activity.finish()
        }
      }
    }
  }
}
