package co.kenrg.yarnover.api.query

enum class LibraryQueryType(private val paramValue: String) {
  PATTERNS("patterns"),
  TAGS("tags");

  override fun toString(): String {
    return this.paramValue
  }
}
