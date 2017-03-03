package co.kenrg.yarnover.facets.hotrightnow.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import co.kenrg.yarnover.R
import co.kenrg.yarnover.api.domain.Pattern
import co.kenrg.yarnover.ext.loadImg
import co.kenrg.yarnover.iface.adapter.DelegateAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView

class PatternDelegateAdapter(
    val onPatternClick: (Pattern) -> Unit
) : DelegateAdapter<ViewItem> {
  override fun onCreateViewHolder(parent: ViewGroup) =
      PatternViewHolder(PatternItemComponent(parent))

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewItem) =
      (holder as PatternViewHolder).bind(item as ViewItem.Pattern)

  inner class PatternViewHolder(
      val patternItemComponent: PatternItemComponent
  ) : RecyclerView.ViewHolder(patternItemComponent.inflate()) {
    fun bind(item: ViewItem.Pattern) {
      patternItemComponent.bindPattern(item.pattern, onPatternClick)
    }
  }

  class PatternItemComponent(val parent: ViewGroup) : AnkoComponent<ViewGroup> {
    lateinit var card: CardView
    lateinit var title: TextView
    lateinit var author: TextView
    lateinit var image: ImageView

    fun inflate() = createView(AnkoContext.create(parent.context, parent))

    fun bindPattern(pattern: Pattern, onPatternClick: (Pattern) -> Unit) {
      this.title.text = pattern.name
      this.author.text = "by ${pattern.designer.name}"
      this.image.loadImg(pattern.firstPhoto.squareUrl)
      this.card.onClick {
        onPatternClick(pattern)
      }
    }

    override fun createView(ui: AnkoContext<ViewGroup>): View {
      val imageViewId = 1

      return with(ui) {
        card = cardView(R.style.AppTheme_CardView) {
          layoutParams = FrameLayout.LayoutParams(matchParent, dip(120)).apply {
            setMargins(dip(12), dip(12), dip(12), 0)
          }

          cardElevation = dip(2).toFloat()

          relativeLayout {
            image = imageView {
              id = imageViewId
              backgroundColor = Color.LTGRAY
            }.lparams(width = dip(120), height = dip(120)) {
              alignParentStart()
              alignParentTop()
            }

            verticalLayout {
              padding = dip(8)

              title = textView(R.style.AppTheme_CardViewTitleText) {
                typeface = sansSerifLight(Typeface.NORMAL)
              }

              author = textView(R.style.AppTheme_CardViewSubtitleText) {
                typeface = sansSerifLight(Typeface.ITALIC)
              }
            }.lparams(height = dip(120)) {
              rightOf(image)
              alignParentEnd()
            }
          }
        }
        card
      }
    }
  }
}

private fun TextView.sansSerifLight(style: Int) = Typeface.create("sans-serif-light", style)
