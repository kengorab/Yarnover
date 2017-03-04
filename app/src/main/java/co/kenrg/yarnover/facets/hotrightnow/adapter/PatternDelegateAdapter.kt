package co.kenrg.yarnover.facets.hotrightnow.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.kenrg.yarnover.R
import co.kenrg.yarnover.ext.loadImg
import co.kenrg.yarnover.iface.adapter.DelegateAdapter
import kotlinx.android.synthetic.main.component_patterncard.view.*

class PatternDelegateAdapter(val onPatternClick: (ViewItem.Pattern, View) -> Unit) : DelegateAdapter<ViewItem> {
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    return PatternViewHolder(layoutInflater.inflate(R.layout.component_patterncard, parent, false))
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewItem) {
    holder as PatternViewHolder
    item as ViewItem.Pattern
    holder.bind(item, onPatternClick)
  }

  class PatternViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(pattern: ViewItem.Pattern, onPatternClick: (ViewItem.Pattern, View) -> Unit) {
      view.patternTitle.text = pattern.patternName
      view.patternAuthor.text = "by ${pattern.designerName}"
      view.previewImage.loadImg(pattern.photoUrl)
      view.patternCard.setOnClickListener { onPatternClick(pattern, view) }
    }
  }
}
