package co.kenrg.yarnover.facets.patternview

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM
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
  private var urlContainsPdf = false
  lateinit private var patternName: String
  lateinit private var patternDesigner: String
  lateinit private var patternDownloadUrl: String

  companion object {
    val MY_PERMISSIONS_REQUEST_WRITE_EXT_STORAGE = 24 // Arbitrary number
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
    patternName = intent.getStringExtra(KEY_PATTERN_NAME)
    patternDesigner = intent.getStringExtra(KEY_PATTERN_DESIGNER_NAME)
    supportActionBar?.title = patternName
    supportActionBar?.subtitle = patternDesigner

    if (!intent.hasExtra(KEY_PATTERN_DOWNLOAD_URL)) finish()
    patternDownloadUrl = intent.getStringExtra(KEY_PATTERN_DOWNLOAD_URL)
    loadPdfIfPossible(patternDownloadUrl) { isPdf, inputStream ->
      urlContainsPdf = isPdf

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

  private fun loadPdfIfPossible(pdfUrl: String, onComplete: (Boolean, InputStream?) -> Unit) {
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

  private fun handleDownloadPdf() {
    if (!urlContainsPdf) {
      // TODO: Show snackbar, with option to go to website
      Toast.makeText(this, "Link was not a pdf download... cannot display", LENGTH_SHORT).show()
    } else {
      if (checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_WRITE_EXT_STORAGE)
      } else {
        downloadPdf()
      }
    }
  }

  private fun downloadPdf() {
    val request = DownloadManager.Request(Uri.parse(patternDownloadUrl))
    request.setTitle(patternName)
    request.allowScanningByMediaScanner()
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$patternName.pdf")

    val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    manager.enqueue(request)
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when (requestCode) {
      MY_PERMISSIONS_REQUEST_WRITE_EXT_STORAGE -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          downloadPdf()
        }
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val downloadMenuItem = menu?.add("Download PDF")
    downloadMenuItem?.setShowAsAction(SHOW_AS_ACTION_IF_ROOM)
    downloadMenuItem?.setIcon(R.drawable.ic_file_download)
    downloadMenuItem?.icon?.apply {
      mutate()
      setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
    }
    downloadMenuItem?.setOnMenuItemClickListener {
      handleDownloadPdf()
      true
    }
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    android.R.id.home -> {
      onBackPressed()
      true
    }
    else -> super.onOptionsItemSelected(item)
  }
}