package co.kenrg.yarnover.facets.patterndetails

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import co.kenrg.yarnover.R
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import kotlinx.android.synthetic.main.activity_patterndetails.*

class PatternDetailsActivity : AppCompatActivity() {
  companion object {
    val KEY_PATTERN_DATA = "PATTERN_DATA"
  }

  private val activity: PatternDetailsActivity = this

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_patterndetails)

    if (!intent.hasExtra(KEY_PATTERN_DATA))
      finish()

    val basicPatternInfo = intent.getParcelableExtra<ViewItem.Pattern>(KEY_PATTERN_DATA)

    toolbar.apply {
      activity.setSupportActionBar(this)
      activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    activity.supportActionBar?.title = basicPatternInfo.patternName
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    android.R.id.home -> {
      onBackPressed()
      true
    }
    else -> super.onOptionsItemSelected(item)
  }
}