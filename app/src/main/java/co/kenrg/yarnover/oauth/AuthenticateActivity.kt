package co.kenrg.yarnover.oauth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import co.kenrg.yarnover.facets.hotrightnow.HotRightNowActivity
import co.kenrg.yarnover.oauth.OAuthManager.VERIFIER_CODE_QUERY_PARAM
import org.jetbrains.anko.doAsync

class AuthenticateActivity : AppCompatActivity() {
  private lateinit var context: Context

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    context = this

    tryToLaunchMainActivity(intent)
  }

  private fun tryToLaunchMainActivity(intent: Intent) {
    val data = intent.data
    if (data != null) {
      val verifierCode = data.getQueryParameter(VERIFIER_CODE_QUERY_PARAM)

      if (verifierCode != null) {
        doAsync {
          val success = OAuthManager.getAndSetAccessToken(context, verifierCode)
          if (success) {
            val mainActivity = Intent(context, HotRightNowActivity::class.java)
            startActivity(mainActivity)
            finish()
          } else {
            Toast.makeText(context, "Incorrect verification code!", Toast.LENGTH_LONG).show()
          }
        }
      }
    } else {
      // Something weird has happened, so we'll try starting the OAuth cycle again
      startActivity(Intent(this, OAuthActivity::class.java))
      finish()
    }
  }
}