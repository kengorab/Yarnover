package co.kenrg.yarnover.facets.hotrightnow.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.kenrg.yarnover.iface.adapter.DelegateAdapter
import org.jetbrains.anko.*

class PatternDelegateAdapter : DelegateAdapter<ViewItem> {
  override fun onCreateViewHolder(parent: ViewGroup) =
      PatternViewHolder(PatternItemComponent(parent))

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewItem) =
      (holder as PatternViewHolder).bind(item as ViewItem.Pattern)

  inner class PatternViewHolder(
      val patternItemComponent: PatternItemComponent
  ) : RecyclerView.ViewHolder(patternItemComponent.inflate()) {
    fun bind(item: ViewItem.Pattern) {
      patternItemComponent.bindNewsItem(item)
    }
  }

  class PatternItemComponent(val parent: ViewGroup) : AnkoComponent<ViewGroup> {
    lateinit var title: TextView

    fun inflate() = createView(AnkoContext.create(parent.context, parent))

    fun bindNewsItem(item: ViewItem.Pattern) {
      this.title.text = item.pattern.name
    }

    override fun createView(ui: AnkoContext<ViewGroup>): View {
      return with(ui) {
        verticalLayout {
          padding = dip(5)

          title = textView {
            textColor = Color.parseColor("#040404")
            textSize = 15f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
          }
        }
      }
    }
  }
}