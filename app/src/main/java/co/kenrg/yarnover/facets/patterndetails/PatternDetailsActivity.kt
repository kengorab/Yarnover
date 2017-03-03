package co.kenrg.yarnover.facets.patterndetails

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.app.AppCompatActivity
import android.view.View
import co.kenrg.yarnover.R
import co.kenrg.yarnover.ext.actionBarSize
import org.jetbrains.anko.UI
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

class PatternDetailsActivity : AppCompatActivity() {

  private fun makeLayout(activity: PatternDetailsActivity): View =
      activity.UI {
        coordinatorLayout {
          fitsSystemWindows = true

          appBarLayout {
            lparams(width = matchParent, height = wrapContent)

            toolbar(R.style.AppTheme_PopupOverlay) {
              activity.setSupportActionBar(this)
              activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }.lparams(width = matchParent, height = actionBarSize()) {
              scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS
            }
          }

          frameLayout {
            backgroundColor = getColor(activity, R.color.backgroundColor)

          }.lparams(width = matchParent, height = matchParent) {
            behavior = AppBarLayout.ScrollingViewBehavior()
          }
        }
      }.view

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(makeLayout(this))
  }
}