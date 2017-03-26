package co.kenrg.yarnover.facets.hotrightnow

import android.os.Bundle
import co.kenrg.yarnover.R
import co.kenrg.yarnover.ext.setTaskDescription
import co.kenrg.yarnover.ui.drawer.BaseDrawerActivity
import kotlinx.android.synthetic.main.activity_hotrightnow.*

class HotRightNowActivity : BaseDrawerActivity() {
  private val activity: HotRightNowActivity = this

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_hotrightnow)
    setTaskDescription()
    setupDrawer(drawerLayout, toolbar, drawerNavigation)

    toolbar.apply {
      activity.setSupportActionBar(this)
      activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
      activity.supportActionBar?.setHomeButtonEnabled(true)
    }
  }
}
