package co.kenrg.yarnover.facets.hotrightnow.adapter

interface ViewType {
  enum class Type {
    LOADING,
    PATTERN
  }

  fun getViewType(): Type
}
