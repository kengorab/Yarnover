package co.kenrg.yarnover.settings

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import co.kenrg.yarnover.Yarnover

object SharedSettingsManager {
  private val prefFileName = "YarnoverPrefs"

  private val KEY_CONSUMER_TOKEN = "consumerToken"
  private val KEY_CONSUMER_SECRET = "consumerSecret"

  private fun withPrefsEditor(applyToEditor: SharedPreferences.Editor.() -> Unit): Boolean {
    val editor = Yarnover.appContext.getSharedPreferences(prefFileName, MODE_PRIVATE).edit()
    editor.apply(applyToEditor)
    return editor.commit()
  }

  fun setApiTokenAndSecret(token: String, secret: String) =
      withPrefsEditor {
        putString(KEY_CONSUMER_TOKEN, token)
        putString(KEY_CONSUMER_SECRET, secret)
      }

  fun clearApiTokenAndSecret() = setApiTokenAndSecret("", "")

  fun getApiTokenAndSecret(): Triple<Boolean, String, String> {
    val prefs = Yarnover.appContext.getSharedPreferences(prefFileName, MODE_PRIVATE)
    val token = prefs.getString(KEY_CONSUMER_TOKEN, "")
    val secret = prefs.getString(KEY_CONSUMER_SECRET, "")

    val isEmpty = token.isNotEmpty() && secret.isNotEmpty()
    return Triple(isEmpty, token, secret)
  }
}