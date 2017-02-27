package co.kenrg.yarnover

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.v7.app.AppCompatActivity
import android.view.View
import co.kenrg.yarnover.ext.actionBarSize
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout

class MainActivity : AppCompatActivity() {

  private fun makeLayout(mainActivity: MainActivity): View =
      mainActivity.UI {
        coordinatorLayout {
          fitsSystemWindows = true

          appBarLayout {
            lparams(width = matchParent, height = wrapContent)

            toolbar(R.style.AppTheme_PopupOverlay) {
              lparams(width = matchParent, height = actionBarSize())

              mainActivity.setSupportActionBar(this)
              mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }.lparams {
              scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS
            }
          }

          frameLayout {
            textView {
              text = "Hello World!"
            }
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
