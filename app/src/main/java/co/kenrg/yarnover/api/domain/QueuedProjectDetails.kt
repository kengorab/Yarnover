package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class QueuedProjectDetails(
    @Json(name = "id") val id: Long,
    @Json(name = "best_photo") val bestPhoto: Photo,
    @Json(name = "created_at") val createdAt: Any, // Not a real date, but a string in yyyy-mm-dd
//    @Json(name = "finish_by") val finishBy: Any,
//    @Json(name = "make_for") val makeFor: String,
    @Json(name = "name") val name: String,
//    @Json(name = "notes_html") val notesHtml: String,
    @Json(name = "notes") val notes: Any, // TODO - type for notes?
//    @Json(name = "pattern") val pattern: Pattern,
    @Json(name = "pattern_id") val patternId: String,
    @Json(name = "pattern_name") val patternName: String,
    @Json(name = "position_in_queue") val positionInQueue: Int,
    @Json(name = "short_pattern_name") val shortPatternName: String,
//    @Json(name = "queued_stashes") val queuedStashes: List<Any>, // TODO - type for Stash
    @Json(name = "skeins") val skeins: Int?,
    @Json(name = "sort_order") val sortOrder: Int,
//    @Json(name = "start_on") val startOn: Date,
    @Json(name = "user_id") val userId: Int,
//    @Json(name = "yarn") val yarn: Any, // TODO - type for Yarn
    @Json(name = "yarn_id") val yarnId: String,
    @Json(name = "yarn_name") val yarnName: String
)