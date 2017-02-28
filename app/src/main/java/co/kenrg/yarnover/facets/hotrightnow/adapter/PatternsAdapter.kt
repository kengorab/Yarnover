package co.kenrg.yarnover.facets.hotrightnow.adapter

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup
import java.util.*

class PatternsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  private var items: ArrayList<ViewType> = ArrayList()
  private var delegateAdapters = SparseArray<ViewTypeDelegateAdapter>()

  init {
    items.add(LoadingViewItem)

    delegateAdapters.put(ViewType.Type.LOADING.ordinal + 1, LoadingDelegateAdapter())
    delegateAdapters.put(ViewType.Type.PATTERN.ordinal + 1, PatternDelegateAdapter())
  }

  fun addPatterns(patterns: List<PatternViewItem>) {
    val initPosition = items.size - 1
    items.removeAt(initPosition)
    notifyItemRemoved(initPosition)

    items.addAll(patterns)
    items.add(LoadingViewItem)
    notifyItemRangeChanged(initPosition, items.size + 1 /* plus loading item */)
  }

  fun getPatterns(): List<PatternViewItem> {
    return items
        .filter { it.getViewType() == ViewType.Type.PATTERN }
        .map { it as PatternViewItem }
  }

  fun replaceWithPatterns(news: List<PatternViewItem>) {
    items.clear()
    val lastPosition = if (items.lastIndex == -1) 0 else items.lastIndex
    notifyItemRangeRemoved(0, lastPosition)

    items.addAll(news)
    items.add(LoadingViewItem)
    notifyItemRangeInserted(0, items.size)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    delegateAdapters[getItemViewType(position)].onBindViewHolder(holder, this.items[position])
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return delegateAdapters[viewType].onCreateViewHolder(parent)
  }

  override fun getItemCount() = items.size

  override fun getItemViewType(position: Int): Int {
    return this.items[position].getViewType().ordinal + 1
  }
}