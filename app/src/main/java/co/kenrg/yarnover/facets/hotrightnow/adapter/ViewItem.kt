package co.kenrg.yarnover.facets.hotrightnow.adapter

import co.kenrg.yarnover.iface.adapter.DelegateViewItem

sealed class ViewItem(viewType: ViewType) : DelegateViewItem<ViewItem.ViewType>(viewType) {
  enum class ViewType(val type: Int) {
    LOADING(0), PATTERN(1);

    companion object {
      fun byType(type: Int) = values().find { it.type == type } ?: LOADING
    }
  }

  object Loading : ViewItem(ViewType.LOADING)
  class Pattern(
      val id: Int,
      val name: String
  ) : ViewItem(ViewType.PATTERN)
}