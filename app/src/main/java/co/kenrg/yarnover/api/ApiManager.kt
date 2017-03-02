package co.kenrg.yarnover.api

import co.kenrg.yarnover.oauth.OAuthManager
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiManager {
  private var INSTANCE: RavelryApi? = null

  private fun initWithSignedClient() {
    if (INSTANCE != null)
      return

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.ravelry.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(OAuthManager.getSignedClient())
        .build()
    INSTANCE = retrofit.create(RavelryApi::class.java)
  }

  fun api(): RavelryApi {
    if (INSTANCE == null)
      initWithSignedClient()

    return INSTANCE!!
  }
}