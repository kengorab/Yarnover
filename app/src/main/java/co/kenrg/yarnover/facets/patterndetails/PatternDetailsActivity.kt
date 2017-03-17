package co.kenrg.yarnover.facets.patterndetails

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import co.kenrg.yarnover.R
import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.api.domain.PatternDetails
import co.kenrg.yarnover.api.request.QueuedProject
import co.kenrg.yarnover.api.request.Volume
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

    patternImage.loadImg(basicPatternInfo.photoUrl, onSuccess = { it.startPostponedTransition(this) })

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
    setupFabMenu(parcel)

    detailsLoading.visibility = View.GONE
    detailsContainer.visibility = View.VISIBLE
  }

  fun setupFabMenu(patternDetails: PatternDetailsParcel) {
    fun libraryHandler(isInLibrary: Boolean) {
      this.patternDetails = patternDetails.copy(isInLibrary = isInLibrary)
      Toast.makeText(this, if (isInLibrary) "Added to Library!" else "Removed from Library!", LENGTH_SHORT).show()
      setupFabMenu(this.patternDetails!!)
    }

    fabAddToLibrary.apply {
      val isInLibrary = patternDetails.isInLibrary

      labelVisibility = View.VISIBLE
      labelText = if (isInLibrary) "Remove from Library" else "Add to Library"
      setImageResource(if (isInLibrary) R.drawable.ic_remove_circle else R.drawable.ic_library_add)
      setOnClickListener {
        fab.close(true)

        if (isInLibrary)
          removeFromLibrary(patternDetails.patternName) { _ -> libraryHandler(isInLibrary = false) }
        else
          addToLibrary(patternDetails.patternId) { _ -> libraryHandler(isInLibrary = true) }
      }
    }

    fun queueHandler(isInQueue: Boolean) {
      this.patternDetails = patternDetails.copy(isQueued = isInQueue)
      Toast.makeText(this, if (isInQueue) "Added to Queue!" else "Removed from Queue!", LENGTH_SHORT).show()
      setupFabMenu(this.patternDetails!!)
    }

    fabAddToQueue.apply {
      val isQueued = patternDetails.isQueued

      labelVisibility = View.VISIBLE
      labelText = if (isQueued) "Remove from Queue" else "Add to Queue"
      setImageResource(if (isQueued) R.drawable.ic_remove_circle else R.drawable.ic_queue_add)
      setOnClickListener {
        fab.close(true)

        if (isQueued)
          removeFromQueue(patternDetails.patternId) { _ -> queueHandler(isInQueue = false) }
        else
          addToQueue(patternDetails.patternId) { _ -> queueHandler(isInQueue = true) }
      }
    }

//    fabAddToFavorites.labelVisibility = View.VISIBLE
//    fabAddToFavorites.labelText = if (parcel.isFavorite) "Remove from Favorites" else "Add to Favorites"
//    fabAddToFavorites.setOnClickListener {
//      fab.close(true)
//      Toast.makeText(this, "Added to Favorites!", LENGTH_SHORT).show()
//    }
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

  fun addToLibrary(id: Long, onSuccess: (Map<String, Any>) -> Unit) {
    doAsync {
      val response = ravelryApi.addToLibrary(Volume(patternId = id)).execute()

      uiThread {
        if (!response.isSuccessful) {
          // TODO - Replace this with snackbar, prompting user to retry request
          Log.e("ASDF", response.message())
          Toast.makeText(this@PatternDetailsActivity, "Error adding pattern to library...", LENGTH_LONG).show()
        } else onSuccess(response.body())
      }
    }
  }

  fun removeFromLibrary(patternName: String, onSuccess: (Map<String, Any>) -> Unit) {
    doAsync {
      // TODO - Use real username
      val librarySearchResponse = ravelryApi.searchLibrary("roboguy12", patternName).execute()

      if (!librarySearchResponse.isSuccessful) {
        uiThread {
          // TODO - Replace this with snackbar, prompting user to retry request
          Log.e("ASDF", librarySearchResponse.message())
          Toast.makeText(this@PatternDetailsActivity, "Error removing pattern from library...", LENGTH_LONG).show()
        }
      } else if (librarySearchResponse.body().paginator.pageCount == 0) {
        Toast.makeText(this@PatternDetailsActivity, "Error removing pattern from library...", LENGTH_LONG).show()
      } else {
        val volumeIdForPattern = librarySearchResponse.body().volumes[0].id
        val response = ravelryApi.removeFromLibrary(volumeIdForPattern).execute()

        uiThread {
          if (!response.isSuccessful) {
            // TODO - Replace this with snackbar, prompting user to retry request
            Log.e("ASDF", response.message())
            Toast.makeText(this@PatternDetailsActivity, "Error removing pattern from library...", LENGTH_LONG).show()
          } else onSuccess(response.body())
        }
      }
    }
  }

  fun addToQueue(patternId: Long, onSuccess: (Map<String, Any>) -> Unit) {
    doAsync {
      val response = ravelryApi.addToQueue("roboguy12", QueuedProject(patternId)).execute()

      uiThread {
        if (!response.isSuccessful) {
          // TODO - Replace this with snackbar, prompting user to retry request
          Log.e("ASDF", response.message())
          Toast.makeText(this@PatternDetailsActivity, "Error adding pattern to library...", LENGTH_LONG).show()
        } else onSuccess(response.body())
      }
    }
  }

  fun removeFromQueue(patternId: Long, onSuccess: (Map<String, Any>) -> Unit) {
    doAsync {
      // TODO - Use real username
      val queueSearchResponse = ravelryApi.getQueue("roboguy12", patternId).execute()

      if (!queueSearchResponse.isSuccessful) {
        uiThread {
          // TODO - Replace this with snackbar, prompting user to retry request
          Log.e("ASDF", queueSearchResponse.message())
          Toast.makeText(this@PatternDetailsActivity, "Error removing pattern from queue...", LENGTH_LONG).show()
        }
      } else if (queueSearchResponse.body().paginator.pageCount == 0) {
        Toast.makeText(this@PatternDetailsActivity, "Error removing pattern from queue...", LENGTH_LONG).show()
      } else {
        val queuedProjectId = queueSearchResponse.body().queuedProjects[0].id
        val response = ravelryApi.removeFromQueue("roboguy12", queuedProjectId).execute()

        uiThread {
          if (!response.isSuccessful) {
            // TODO - Replace this with snackbar, prompting user to retry request
            Log.e("ASDF", response.message())
            Toast.makeText(this@PatternDetailsActivity, "Error removing pattern from queue...", LENGTH_LONG).show()
          } else onSuccess(response.body())
        }
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