package co.kenrg.yarnover.api.query

enum class SortOrder(private val paramValue: String) {
  BEST_MATCH("best"),
  HOT_RIGHT_NOW("recently-popular"),
  NAME("name"),
  MOST_POPULAR("popularity"),
  MOST_PROJECTS("projects"),
  MOST_FAVORITES("favorites"),
  MOST_QUEUED("queues"),
  PUBLICATION_DATE("date"),
  NEW_TO_RAVELRY("created"),
  RATING("rating"),
  DIFFICULTY("difficulty"),
  YARN_REQUIRED("yarn");

  override fun toString(): String {
    return this.paramValue
  }
}
