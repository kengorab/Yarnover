package co.kenrg.yarnover.facets.patterndetails

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.kenrg.yarnover.R
import kotlinx.android.synthetic.main.activity_patterndetails.*

class PatternDetailsActivity : AppCompatActivity() {
  private val activity: PatternDetailsActivity = this

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_patterndetails)

    toolbar.apply {
      activity.setSupportActionBar(this)
      activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
  }
}