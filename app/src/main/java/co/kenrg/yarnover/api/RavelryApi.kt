package co.kenrg.yarnover.api

import retrofit2.Call
import retrofit2.http.GET

interface RavelryApi {
  data class CurrentUser(
      val small_photo_url: String,
      val tiny_photo_url: String,
      val id: Long,
      val large_photo_url: String,
      val photo_url: String,
      val username: String
  )

  data class CurrentUserResponse(val user: CurrentUser)

  @GET("current_user.json")
  fun getCurrentUser(): Call<CurrentUserResponse>
}