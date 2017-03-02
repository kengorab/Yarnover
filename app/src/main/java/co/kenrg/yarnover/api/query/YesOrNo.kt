package co.kenrg.yarnover.api.query

enum class YesOrNo {
  YES,
  NO;

  override fun toString(): String {
    return this.name.toLowerCase()
  }
}