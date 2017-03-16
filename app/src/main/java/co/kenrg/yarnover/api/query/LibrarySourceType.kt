package co.kenrg.yarnover.api.query

enum class LibrarySourceType(private val paramValue: String) {
  BOOK("book"),
  MAGAZINE("magazine"),
  BOOKLET("booklet"),
  PATTERN("pattern"),
  PDF("pdf");

  override fun toString(): String {
    return this.paramValue
  }
}
