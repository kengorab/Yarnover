package co.kenrg.yarnover.facets.hotrightnow.adapter

import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.*

class LoadingDelegateAdapter : ViewTypeDelegateAdapter {
  override fun onCreateViewHolder(parent: ViewGroup) = LoadingViewHolder(LoadingComponent(parent))

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {}

  class LoadingViewHolder(component: LoadingComponent) : RecyclerView.ViewHolder(component.inflate())

  class LoadingComponent(val parent: ViewGroup) : AnkoComponent<ViewGroup> {
    fun inflate() = createView(AnkoContext.create(parent.context, parent))

    override fun createView(ui: AnkoContext<ViewGroup>): View {
      return with(ui) {
        linearLayout {
          lparams(width = matchParent, height = wrapContent) {
            margin = dip(20)
          }
          gravity = Gravity.CENTER_HORIZONTAL

          progressBar {
            lparams(width = dip(40), height = dip(40))
          }
        }
      }
    }
  }
}
