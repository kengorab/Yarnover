package co.kenrg.yarnover.facets.patterndetails

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import co.kenrg.yarnover.R
import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.api.domain.PatternDetails
import co.kenrg.yarnover.ext.checkPermission
import co.kenrg.yarnover.ext.downloadFile
import co.kenrg.yarnover.ext.loadImg
import co.kenrg.yarnover.ext.startPostponedTransition
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity.Companion.KEY_PATTERN_DESIGNER_NAME
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity.Companion.KEY_PATTERN_DOWNLOAD_URL
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity.Companion.KEY_PATTERN_NAME
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity.Companion.MY_PERMISSIONS_REQUEST_WRITE_EXT_STORAGE
import kotlinx.android.synthetic.main.activity_patterndetails.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class PatternDetailsActivity : AppCompatActivity() {
  companion object {
    private val KEY_PARCEL = "PATTERN_DETAILS_ACTIVITY_PARCEL"
    val KEY_PATTERN_DATA = "PATTERN_DATA"
  }

  private val activity: PatternDetailsActivity = this
  private val ravelryApi = api()
  private var patternDetails: PatternDetailsParcel? = null

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    if (patternDetails != null)
      outState?.putParcelable(KEY_PARCEL, patternDetails)
  }

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

    if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PARCEL)) {
      val parcel = savedInstanceState.get(KEY_PARCEL) as PatternDetailsParcel
      initWithParcel(parcel)
    } else {
      requestPatternDetails(basicPatternInfo.id) { patternDetails, urlIsPdf ->
        initWithParcel(PatternDetailsParcel(patternDetails, urlIsPdf))
      }
    }
  }

  fun initWithParcel(parcel: PatternDetailsParcel) {
    this.patternDetails = parcel
    detailsLoading.visibility = View.GONE
    detailsContainer.visibility = View.VISIBLE
    downloadPdf.visibility = if (parcel.urlIsPdf) View.VISIBLE else View.GONE
  }

  fun requestPatternDetails(id: Long, onSuccess: (PatternDetails, Boolean) -> Unit) {
    doAsync {
      val response = ravelryApi.getPatternById(id).execute()
      val body =
          if (response.isSuccessful) response.body().pattern
          else null

      val urlIsPdf =
          if (body == null) false
          else {
            val url = URL(body.downloadLocation.url)
            val connection = url.openConnection()
            connection.connect()

            connection.contentType == "application/pdf"
          }

      uiThread {
        if (!response.isSuccessful) {
          // TODO - Replace this with snackbar, prompting user to retry request
          Toast.makeText(this@PatternDetailsActivity, "Error fetching pattern details...", LENGTH_LONG).show()
        } else onSuccess(body!!, urlIsPdf)
      }
    }
  }

  fun handleOpenPattern() {
    val intent = Intent(this, PatternPDFViewActivity::class.java)
    intent.putExtra(KEY_PATTERN_NAME, patternDetails?.patternName)
    intent.putExtra(KEY_PATTERN_DESIGNER_NAME, patternDetails?.patternAuthor)
    intent.putExtra(KEY_PATTERN_DOWNLOAD_URL, patternDetails?.downloadUrl)
    startActivity(intent)
  }

  fun handleDownloadPdf() {
    if (this.patternDetails!!.urlIsPdf) {
      checkPermission(WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXT_STORAGE) {
        this.downloadFile(this.patternDetails!!.downloadUrl, this.patternDetails!!.patternName)
      }
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when (requestCode) {
      MY_PERMISSIONS_REQUEST_WRITE_EXT_STORAGE -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          this.downloadFile(this.patternDetails!!.downloadUrl, this.patternDetails!!.patternName)
        }
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