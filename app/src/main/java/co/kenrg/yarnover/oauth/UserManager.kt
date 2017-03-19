package co.kenrg.yarnover.oauth

import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.settings.SharedSettingsManager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

object UserManager {

  fun isUserInSharedPrefs(): Boolean {
    val (exists, _) = SharedSettingsManager.getCurrentUsername()
    return exists
  }

  fun getUsername(): String {
    val (exists, username) = SharedSettingsManager.getCurrentUsername()
    if (!exists)
      throw RuntimeException("Cannot get username from shared prefs")
    return username
  }

  fun storeUsernameInSharedPrefs(then: () -> Unit) {
    doAsync {
      val currentUser = api().getCurrentUser().execute()
      SharedSettingsManager.setCurrentUsername(currentUser.body().user.username)

      uiThread { then() }
    }
  }
}