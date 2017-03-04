package co.kenrg.yarnover.facets.hotrightnow.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class PatternDelegatorAdapter(
    onPatternClick: (ViewItem.Pattern, View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private var items = arrayListOf<ViewItem>(ViewItem.Loading)

  private val loadingDelegateAdapter = LoadingDelegateAdapter()
  private val patternDelegateAdapter = PatternDelegateAdapter(onPatternClick)

  private fun delegateForViewType(viewType: ViewItem.ViewType) = when (viewType) {
    ViewItem.ViewType.LOADING -> loadingDelegateAdapter
    ViewItem.ViewType.PATTERN -> patternDelegateAdapter
  }

  override fun getItemViewType(position: Int) = this.items[position].viewType.type

  fun addPatterns(patterns: List<ViewItem.Pattern>) {
    val initPosition = items.size - 1
    items.removeAt(initPosition)
    notifyItemRemoved(initPosition)

    items.addAll(patterns)
    items.add(ViewItem.Loading)
    notifyItemRangeChanged(initPosition, items.size + 1)  // Add 1 to account for loading view
  }

  fun getPatterns(): List<ViewItem.Pattern> {
    return items
        .filter { it is ViewItem.Pattern }
        .map { it as ViewItem.Pattern }
  }

  fun replaceWithPatterns(patterns: List<ViewItem.Pattern>) {
    items.clear()
    val lastPosition = if (items.lastIndex == -1) 0 else items.lastIndex
    notifyItemRangeRemoved(0, lastPosition)

    items.addAll(patterns)
    items.add(ViewItem.Loading)
    notifyItemRangeInserted(0, items.size)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    delegateForViewType(this.items[position].viewType).onBindViewHolder(holder, this.items[position])
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return delegateForViewType(ViewItem.ViewType.byType(viewType)).onCreateViewHolder(parent)
  }
}