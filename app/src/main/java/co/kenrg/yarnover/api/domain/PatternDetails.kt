package co.kenrg.yarnover.api.domain

import com.squareup.moshi.Json

data class PatternDetails(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "comments_count") val numComments: Int,
    //    @Json(name = "craft") val craft: List<Any>, // TODO - Craft type
    @Json(name = "currency") val currency: String,
    @Json(name = "currency_symbol") val currencySymbol: String?,
    @Json(name = "difficulty_average") val avgDifficulty: Float,
    @Json(name = "difficulty_count") val numDifficultyVotes: Int?,
    @Json(name = "download_location") val downloadLocation: DownloadLocation,
    @Json(name = "downloadable") val isDownloadable: Boolean,
    @Json(name = "favorites_count") val numFavorites: Int,
    @Json(name = "free") val isFree: Boolean,
    @Json(name = "gauge") val gauge: String,
    @Json(name = "gauge_description") val gaugeDesc: String,
    @Json(name = "gauge_divisor") val gaugeDivisor: String,
    @Json(name = "gauge_pattern") val gaugePattern: String,
    @Json(name = "notes_html") val notesHtml: String,
    //    @Json(name = "packs") val packs: Any, // TODO - Pack type
//    @Json(name = "pattern_attributes") val patternAttributes: List<Any>, // TODO - PatternAttribute type
    @Json(name = "pattern_author") val patternAuthor: PatternAuthor,
    @Json(name = "pattern_categories") val patternCategories: List<PatternCategory>,
    @Json(name = "pattern_needle_sizes") val patternNeedleSizes: List<Map<String, Any>>,
    @Json(name = "pdf_in_library") val isPdfInLibrary: Boolean,
    @Json(name = "pdf_url") val pdfUrl: String,
    @Json(name = "permalink") val permalink: String,
    @Json(name = "personal_attributes") val personalAttributes: Map<String, Any>,
    @Json(name = "photos") val photos: List<Photo>,
    @Json(name = "price") val price: String,
    //    @Json(name = "printings") val printings: List<Any>, // TODO - Printing type
    @Json(name = "projects_count") val numProjects: Int,
    @Json(name = "published") val published: String,
    @Json(name = "queued_projects_count") val numQueuedProjects: Int,
    @Json(name = "rating_average") val avgRating: Float,
    @Json(name = "rating_count") val numRatings: Int?,
    @Json(name = "ravelry_download") val isRavelryDownload: Boolean,
    @Json(name = "row_gauge") val rowGauge: String,
    @Json(name = "sizes_available") val sizesAvailable: String,
    @Json(name = "url") val url: String,
    @Json(name = "volumes_in_library") val volumesInLibrary: List<Int>,
    @Json(name = "yardage") val yardage: Int?,
    @Json(name = "yardage_description") val yardageDesc: String,
    @Json(name = "yardage_max") val maxYardage: Int?,
    @Json(name = "yarn_weight") val yarnWeight: YarnWeight,
    @Json(name = "yarn_weight_description") val yarnWeightDesc: String
)