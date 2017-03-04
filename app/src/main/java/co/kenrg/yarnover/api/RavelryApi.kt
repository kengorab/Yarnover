package co.kenrg.yarnover.api

import co.kenrg.yarnover.api.query.Availability
import co.kenrg.yarnover.api.query.SortOrder
import co.kenrg.yarnover.api.query.YesOrNo
import co.kenrg.yarnover.api.query.YesOrNo.YES
import co.kenrg.yarnover.api.response.CurrentUserResponse
import co.kenrg.yarnover.api.response.PaginatedPatternsResponse
import co.kenrg.yarnover.api.response.PatternDetailsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RavelryApi {

  @GET("current_user.json")
  fun getCurrentUser(): Call<CurrentUserResponse>

  @GET("patterns/search.json")
  fun searchPatterns(
      @Query("page") page: Int = 1,
      @Query("page_size") pageSize: Int = 25,
      @Query("photo") hasPhoto: YesOrNo = YES,
      @Query("sort") sort: SortOrder = SortOrder.HOT_RIGHT_NOW,
      @Query("availability") availability: Availability = Availability.FREE,
      @Query("debug") debug: Int = 1
  ): Call<PaginatedPatternsResponse>

  @GET("patterns/{id}.json")
  fun getPatternById(
      @Path("id") patternId: Long,
      @Query("debug") debug: Int = 1
  ): Call<PatternDetailsResponse>
}