package co.kenrg.yarnover.facets.patterndetails

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import co.kenrg.yarnover.R
import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.api.domain.PatternDetails
import co.kenrg.yarnover.ext.loadImg
import co.kenrg.yarnover.ext.startPostponedTransition
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity.Companion.KEY_PATTERN_DESIGNER_NAME
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity.Companion.KEY_PATTERN_DOWNLOAD_URL
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity.Companion.KEY_PATTERN_NAME
import kotlinx.android.synthetic.main.activity_patterndetails.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class PatternDetailsActivity : AppCompatActivity() {
  companion object {
    val KEY_PATTERN_DATA = "PATTERN_DATA"
  }

  private val activity: PatternDetailsActivity = this
  private val ravelryApi = api()
  private lateinit var patternDetails: PatternDetails

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_patterndetails)
    supportPostponeEnterTransition()

    if (!intent.hasExtra(KEY_PATTERN_DATA)) finish()
    val basicPatternInfo = intent.getParcelableExtra<ViewItem.Pattern>(KEY_PATTERN_DATA)

    toolbar.apply {
      activity.setSupportActionBar(this)
      activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    patternName.text = basicPatternInfo.patternName
    authorName.text = basicPatternInfo.designerName

    openPattern.setOnClickListener { handleOpenPattern() }
    downloadPdf.setOnClickListener { handleDownloadPdf() }

    image.loadImg(basicPatternInfo.photoUrl, onSuccess = { it.startPostponedTransition(this) })
    requestPatternDetails(basicPatternInfo.id)
  }

  fun requestPatternDetails(id: Long) {
    doAsync {
      Thread.sleep(1000)
      val response = ravelryApi.getPatternById(id).execute()

      uiThread {
        if (!response.isSuccessful) {
          // TODO - Replace this with snackbar, prompting user to retry request
          Toast.makeText(this@PatternDetailsActivity, "Error fetching pattern details...", LENGTH_LONG).show()
        } else {
          patternDetails = response.body().pattern
        }
      }
    }
  }

  fun handleOpenPattern() {
    val intent = Intent(this, PatternPDFViewActivity::class.java)
    intent.putExtra(KEY_PATTERN_NAME, patternDetails.name)
    intent.putExtra(KEY_PATTERN_DESIGNER_NAME, patternDetails.patternAuthor.name)
    intent.putExtra(KEY_PATTERN_DOWNLOAD_URL, patternDetails.downloadLocation.url)
    startActivity(intent)
  }

  fun handleDownloadPdf() {
    Toast.makeText(this, "Download pattern: ${patternDetails.name}", LENGTH_SHORT).show()
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    android.R.id.home -> {
      onBackPressed()
      true
    }
    else -> super.onOptionsItemSelected(item)
  }
}