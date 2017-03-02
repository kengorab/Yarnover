package co.kenrg.yarnover.api.query

enum class Availability(private val paramValue: String) {
  FREE("free"),
  PURCHASE_ONLINE("online"),
  PURCHASE_IN_PRINT("inprint"),
  RAVELRY_DOWNLOAD("ravelry"),
  IN_LIBRARY("library");

  override fun toString(): String {
    return this.paramValue
  }
}