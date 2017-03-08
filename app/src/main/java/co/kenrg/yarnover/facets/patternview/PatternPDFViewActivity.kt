package co.kenrg.yarnover.facets.patternview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import co.kenrg.yarnover.R
import kotlinx.android.synthetic.main.activity_patternpdfview.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.InputStream
import java.net.URL

class PatternPDFViewActivity : AppCompatActivity() {
  private val activity = this

  companion object {
    val KEY_PATTERN_NAME = "PATTERN_NAME"
    val KEY_PATTERN_DESIGNER_NAME = "PATTERN_DESIGNER_NAME"
    val KEY_PATTERN_DOWNLOAD_URL = "PATTERN_DOWNLOAD_URL"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_patternpdfview)
    toolbar.apply {
      activity.setSupportActionBar(this)
      activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    if (!intent.hasExtra(KEY_PATTERN_NAME)) finish()
    if (!intent.hasExtra(KEY_PATTERN_DESIGNER_NAME)) finish()
    val patternName = intent.getStringExtra(KEY_PATTERN_NAME)
    val patternDesigner = intent.getStringExtra(KEY_PATTERN_DESIGNER_NAME)
    supportActionBar?.title = patternName
    supportActionBar?.subtitle = patternDesigner

    if (!intent.hasExtra(KEY_PATTERN_DOWNLOAD_URL)) finish()
    val patternDownloadUrl = intent.getStringExtra(KEY_PATTERN_DOWNLOAD_URL)
    downloadPDFFromUrl(patternDownloadUrl) { isPdf, inputStream ->
      if (!isPdf) {
        // TODO: Show snackbar, with option to go to website
        Toast.makeText(this, "Link was not a pdf download... cannot display", LENGTH_SHORT).show()
      } else {
        if (inputStream != null) {
          pdfView.fromStream(inputStream)
              .onLoad {
                patternPdfLoading.visibility = GONE
                pdfView.visibility = VISIBLE
              }
              .load()
        }
      }
    }
  }

  private fun downloadPDFFromUrl(pdfUrl: String, onComplete: (Boolean, InputStream?) -> Unit) {
    doAsync {
      val url = URL(pdfUrl)
      val connection = url.openConnection()
      connection.connect()

      val stream =
          if (connection.contentType == "application/pdf") url.openStream()
          else null

      uiThread {
        onComplete(stream != null, stream)
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    android.R.id.home -> {
      onBackPressed()
      true
    }
    else -> super.onOptionsItemSelected(item)
  }
}