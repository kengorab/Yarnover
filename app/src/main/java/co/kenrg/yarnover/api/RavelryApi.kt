package co.kenrg.yarnover.api

import co.kenrg.yarnover.api.query.Availability
import co.kenrg.yarnover.api.query.LibraryQueryType
import co.kenrg.yarnover.api.query.LibrarySourceType
import co.kenrg.yarnover.api.query.SortOrder
import co.kenrg.yarnover.api.query.YesOrNo
import co.kenrg.yarnover.api.query.YesOrNo.YES
import co.kenrg.yarnover.api.request.Bookmark
import co.kenrg.yarnover.api.request.QueuedProject
import co.kenrg.yarnover.api.request.Volume
import co.kenrg.yarnover.api.response.BookmarkResponse
import co.kenrg.yarnover.api.response.CurrentUserResponse
import co.kenrg.yarnover.api.response.PaginatedLibrarySearchResponse
import co.kenrg.yarnover.api.response.PaginatedPatternsResponse
import co.kenrg.yarnover.api.response.PaginatedQueueResponse
import co.kenrg.yarnover.api.response.PatternDetailsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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
      @Query("availability") availability: Availability = Availability.FREE
  ): Call<PaginatedPatternsResponse>

  @GET("patterns/{id}.json")
  fun getPatternById(
      @Path("id") patternId: Long
  ): Call<PatternDetailsResponse>

  @GET("/people/{username}/library/search.json")
  fun searchLibrary(
      @Path("username") username: String,
      @Query("query") query: String,
      @Query("query_type") queryType: LibraryQueryType = LibraryQueryType.PATTERNS,
      @Query("type") type: LibrarySourceType = LibrarySourceType.PATTERN,
      @Query("sort") sort: SortOrder = SortOrder.BEST_MATCH,
      @Query("page") page: Int = 1,
      @Query("page_size") pageSize: Int = 25
  ): Call<PaginatedLibrarySearchResponse>

  @POST("volumes/create.json")
  fun addToLibrary(
      @Body volume: Volume
  ): Call<Map<String, Any>>

  @DELETE("volumes/{id}.json")
  fun removeFromLibrary(
      @Path("id") patternId: Long
  ): Call<Map<String, Any>>

  @POST("/people/{username}/queue/create.json")
  fun addToQueue(
      @Path("username") username: String,
      @Body queuedProject: QueuedProject
  ): Call<Map<String, Any>>

  @DELETE("/people/{username}/queue/{queuedProjectId}.json")
  fun removeFromQueue(
      @Path("username") username: String,
      @Path("queuedProjectId") queuedProjectId: Long
  ): Call<Map<String, Any>>

  @GET("/people/{username}/queue/list.json")
  fun getQueue(
      @Path("username") username: String,
      @Query("pattern_id") patternId: Long,
      @Query("query") query: String? = null,
      @Query("query_type") queryType: LibraryQueryType = LibraryQueryType.PATTERNS,
      @Query("page") page: Int = 1,
      @Query("page_size") pageSize: Int = 25
  ): Call<PaginatedQueueResponse>

  @POST("/people/{username}/favorites/create.json")
  fun addToFavorites(
      @Path("username") username: String,
      @Body bookmark: Bookmark
  ): Call<BookmarkResponse>

  @DELETE("/people/{username}/favorites/{id}.json")
  fun removeFromFavorites(
      @Path("username") username: String,
      @Path("id") favoritedId: Long
  ): Call<Map<String, Any>>
}