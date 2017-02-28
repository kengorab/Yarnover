package co.kenrg.yarnover.facets.hotrightnow.adapter

class PatternViewItem(
    val id: Int,
    val name: String
) : ViewType {
  override fun getViewType() = ViewType.Type.PATTERN
}
