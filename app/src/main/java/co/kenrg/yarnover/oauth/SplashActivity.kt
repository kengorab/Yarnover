package co.kenrg.yarnover.oauth

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import co.kenrg.yarnover.R
import co.kenrg.yarnover.facets.hotrightnow.HotRightNowActivity
import org.jetbrains.anko.*

class SplashActivity : AppCompatActivity() {
  private lateinit var splashActivity: Activity

  private fun makeSplashScreen(splashActivity: SplashActivity): View {
    val appLogoViewId = 1
    val primaryColor = getColor(splashActivity, R.color.colorPrimary)

    return splashActivity.UI {
      relativeLayout {
        lparams(width = matchParent, height = matchParent)
        backgroundColor = primaryColor

        verticalLayout {
          id = appLogoViewId

          textView("Yarnover") {
            gravity = Gravity.CENTER
            textColor = Color.WHITE
            textSize = 36f
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
          }

          imageView {
            setImageResource(R.drawable.yarn_ball)
            setColorFilter(Color.WHITE)
          }

          textView("for Ravelry") {
            gravity = Gravity.CENTER
            textColor = Color.WHITE
            textSize = 18f
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
          }

        }.lparams { centerInParent() }

        relativeLayout {

          button("Login to Ravelry") {
            textColor = primaryColor
            backgroundColor = Color.WHITE
            padding = dip(12)

            onClick { getNewAccessToken() }
          }.lparams { centerInParent() }
        }.lparams {
          below(appLogoViewId)
          alignParentBottom()
          centerHorizontally()
        }
      }
    }.view
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    splashActivity = this

    if (OAuthManager.setAccessTokenFromSharedPrefs(this)) {
      startActivity(Intent(this, HotRightNowActivity::class.java))
    } else {
      setContentView(makeSplashScreen(this))
    }
  }

  fun getNewAccessToken() {
    doAsync {
      val authorizeAppUrl = OAuthManager.getAuthorizeAppUrl()
      val uri = Uri.parse(authorizeAppUrl)

      uiThread {
        if (uri != null) {
          val browser = Intent(splashActivity, OAuthActivity::class.java)
          browser.data = uri

          splashActivity.startActivity(browser)
          splashActivity.finish()
        }
      }
    }
  }
}
