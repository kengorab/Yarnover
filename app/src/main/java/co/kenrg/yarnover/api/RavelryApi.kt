package co.kenrg.yarnover.api

import co.kenrg.yarnover.api.response.CurrentUserResponse
import co.kenrg.yarnover.api.response.PaginatedPatternsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RavelryApi {

  @GET("current_user.json")
  fun getCurrentUser(): Call<CurrentUserResponse>

  @GET("patterns/search.json?query=cowl")
  fun searchPatterns(
      @Query("page") page: Int,
      @Query("page_size") pageSize: Int
  ): Call<PaginatedPatternsResponse>
}