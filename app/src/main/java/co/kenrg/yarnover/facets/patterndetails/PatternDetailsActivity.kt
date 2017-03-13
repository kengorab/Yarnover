package co.kenrg.yarnover.facets.patterndetails

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import co.kenrg.yarnover.R
import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.api.domain.PatternDetails
import co.kenrg.yarnover.ext.addRow
import co.kenrg.yarnover.ext.checkPermission
import co.kenrg.yarnover.ext.downloadFile
import co.kenrg.yarnover.ext.format
import co.kenrg.yarnover.ext.loadImg
import co.kenrg.yarnover.ext.startPostponedTransition
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity.Companion.KEY_PATTERN_DESIGNER_NAME
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity.Companion.KEY_PATTERN_DOWNLOAD_URL
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity.Companion.KEY_PATTERN_NAME
import co.kenrg.yarnover.facets.patternview.PatternPDFViewActivity.Companion.MY_PERMISSIONS_REQUEST_WRITE_EXT_STORAGE
import co.kenrg.yarnover.utils.DateUtils
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
  private var patternDetailsTableExpanded = true

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

    detailsTableToggle.setOnClickListener {
      detailsTableToggleIcon.animate()
          .rotationBy(180f)
          .setInterpolator(AccelerateDecelerateInterpolator())
          .setDuration(200)
          .start()

      patternDetailsTable.visibility = if (patternDetailsTableExpanded) View.GONE else View.VISIBLE
      patternDetailsTable.requestLayout()
      patternDetailsTableExpanded = !patternDetailsTableExpanded
    }

    patternImage.loadImg(basicPatternInfo.photoUrl, onSuccess = { it.startPostponedTransition(this) })

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
    downloadPdf.visibility = if (parcel.urlIsPdf) View.VISIBLE else View.GONE
    setupPatternDetailsTable()

    detailsLoading.visibility = View.GONE
    detailsContainer.visibility = View.VISIBLE
  }

  fun setupPatternDetailsTable() {
    patternDetailsTable.removeAllViewsInLayout()

    if (this.patternDetails == null) return

    val patternDetails = this.patternDetails!!
    val tableRows = arrayListOf<Pair<String, String>>().apply {
      if (patternDetails.patternSource != null && patternDetails.patternSource.isNotBlank())
        this.add(Pair("Published In", patternDetails.patternSource))

      if (patternDetails.craft.isNotBlank())
        this.add(Pair("Craft", patternDetails.craft))

      if (patternDetails.categories.isNotEmpty())
        this.add(Pair("Categories", patternDetails.categories.joinToString(", ")))

      if (patternDetails.publishedDate.isNotBlank()) {
        val parsedDate = DateUtils.parse(patternDetails.publishedDate)
        if (parsedDate != null && parsedDate.format() != null)
          this.add(Pair("Published", parsedDate.format()!!))
      }

      if (patternDetails.yarnWeightDesc.isNotBlank())
        this.add(Pair("Yarn Weight", patternDetails.yarnWeightDesc))

      if (patternDetails.gaugeDesc.isNotBlank())
        this.add(Pair("Gauge", patternDetails.gaugeDesc))

      if (patternDetails.needleSizes.isNotEmpty())
        this.add(Pair("Needle Size", patternDetails.needleSizes.joinToString(", ")))

      if (patternDetails.yardageDesc.isNotBlank())
        this.add(Pair("Yardage", patternDetails.yardageDesc))
    }

    tableRows.forEach { (col1, col2) ->
      patternDetailsTable.addRow(col1, col2)
    }
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