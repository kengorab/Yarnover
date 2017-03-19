package co.kenrg.yarnover.oauth

import android.util.Log
import co.kenrg.yarnover.R
import co.kenrg.yarnover.Yarnover
import co.kenrg.yarnover.settings.SharedSettingsManager
import oauth.signpost.exception.OAuthCommunicationException
import oauth.signpost.exception.OAuthExpectationFailedException
import oauth.signpost.exception.OAuthMessageSignerException
import oauth.signpost.exception.OAuthNotAuthorizedException
import okhttp3.OkHttpClient
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.OkHttpOAuthProvider
import se.akerfeldt.okhttp.signpost.SigningInterceptor

object OAuthManager {
  val AUTH_CALLBACK_URL = "http://kenrg.co/yarnover/auth-callback"
  val VERIFIER_CODE_QUERY_PARAM = "oauth_verifier"

  private val logTag = OAuthManager::class.java.javaClass.name

  private val ravelryAuthKey = Yarnover.appContext.getString(R.string.ravelry_auth_key)
  private val ravelrySecretKey = Yarnover.appContext.getString(R.string.ravelry_secret_key)

  private val baseUrl = "https://www.ravelry.com"
  private val requestTokenUrl = "$baseUrl/oauth/request_token"
  private val accessTokenUrl = "$baseUrl/oauth/access_token"
  private val authorizeUrl = "$baseUrl/oauth/authorize"

  private val consumer = OkHttpOAuthConsumer(ravelryAuthKey, ravelrySecretKey)
  private val provider = OkHttpOAuthProvider(requestTokenUrl, accessTokenUrl, authorizeUrl)

  @Throws(
      OAuthCommunicationException::class, OAuthExpectationFailedException::class,
      OAuthNotAuthorizedException::class, OAuthMessageSignerException::class
  )
  fun getAuthorizeAppUrl(): String {
    return provider.retrieveRequestToken(consumer, AUTH_CALLBACK_URL)
  }

  fun setAccessTokenFromSharedPrefs(): Boolean {
    val (exists, token, secret) = SharedSettingsManager.getApiTokenAndSecret()
    if (exists) consumer.setTokenWithSecret(token, secret)
    return exists
  }

  fun clearAccessToken(): Boolean {
    consumer.setTokenWithSecret("", "")
    return SharedSettingsManager.clearApiTokenAndSecret()
  }

  fun getAndSetAccessToken(verifyCode: String): Boolean {
    try {
      provider.retrieveAccessToken(consumer, verifyCode)
      val token = consumer.token
      val secret = consumer.tokenSecret
      if (token != null && secret != null) {
        return SharedSettingsManager.setApiTokenAndSecret(token, secret)
      } else {
        return false
      }
    } catch (e: OAuthMessageSignerException) {
      Log.w(logTag, Log.getStackTraceString(e))
    } catch (e: OAuthNotAuthorizedException) {
      Log.w(logTag, Log.getStackTraceString(e))
    } catch (e: OAuthExpectationFailedException) {
      Log.w(logTag, Log.getStackTraceString(e))
    } catch (e: OAuthCommunicationException) {
      Log.w(logTag, Log.getStackTraceString(e))
    }

    return false
  }

  fun getSignedClient(): OkHttpClient {
    if (consumer.token == null || consumer.token.isEmpty()) {
      throw RuntimeException("Cannot get signed client")
    }

    return OkHttpClient.Builder()
        .addInterceptor(SigningInterceptor(consumer))
        .build()
  }
}
