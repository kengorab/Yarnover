package co.kenrg.yarnover.facets.patterndetails

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast.LENGTH_LONG
import co.kenrg.yarnover.R
import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.api.domain.PatternDetails
import co.kenrg.yarnover.api.request.Bookmark
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
import co.kenrg.yarnover.oauth.UserManager
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

  // --------------- View Setup --------------- \\

  fun setupFabMenu(patternDetails: PatternDetailsParcel) {
    fun libraryHandler(isInLibrary: Boolean) {
      this.patternDetails = patternDetails.copy(isInLibrary = isInLibrary)
      if (isInLibrary)
        Snackbar
            .make(container, "Added to Library!", Snackbar.LENGTH_SHORT)
            .show()
      else
        Snackbar
            .make(container, "Removed from Library!", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
              addToLibrary(patternDetails.patternId) {
                libraryHandler(isInLibrary = true)
              }
            }
            .show()

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
          removeFromLibrary(patternDetails.patternName) { libraryHandler(isInLibrary = false) }
        else
          addToLibrary(patternDetails.patternId) { libraryHandler(isInLibrary = true) }
      }
    }

    fun queueHandler(isInQueue: Boolean) {
      this.patternDetails = patternDetails.copy(isQueued = isInQueue)
      if (isInQueue)
        Snackbar
            .make(container, "Added to Queue!", Snackbar.LENGTH_SHORT)
            .show()
      else
        Snackbar
            .make(container, "Removed from Queue!", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
              addToQueue(patternDetails.patternId) {
                queueHandler(isInQueue = true)
              }
            }
            .show()

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
          removeFromQueue(patternDetails.patternId) { queueHandler(isInQueue = false) }
        else
          addToQueue(patternDetails.patternId) { queueHandler(isInQueue = true) }
      }
    }

    fun favoritesHandler(isInFavorites: Boolean, bookmarkId: Long?) {
      this.patternDetails = patternDetails.copy(isFavorite = isInFavorites, bookmarkId = bookmarkId)
      if (isInFavorites)
        Snackbar
            .make(container, "Added to Favorites!", Snackbar.LENGTH_SHORT)
            .show()
      else
        Snackbar
            .make(container, "Removed from Favorites!", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
              addToFavorites(patternDetails.patternId) { bookmarkId ->
                favoritesHandler(isInFavorites = true, bookmarkId = bookmarkId)
              }
            }
            .show()
      setupFabMenu(this.patternDetails!!)
    }

    fabAddToFavorites.apply {
      val isFavorite = patternDetails.isFavorite && patternDetails.bookmarkId != null
      labelVisibility = View.VISIBLE
      labelText = if (isFavorite) "Remove from Favorites" else "Add to Favorites"
      setImageResource(if (isFavorite) R.drawable.ic_remove_circle else R.drawable.ic_favorite_border)
      setOnClickListener {
        fab.close(true)

        if (isFavorite) {
          if (patternDetails.bookmarkId != null)
            removeFromFavorites(patternDetails.bookmarkId) {
              favoritesHandler(isInFavorites = false, bookmarkId = null)
            }
        } else {
          addToFavorites(patternDetails.patternId) { bookmarkId ->
            favoritesHandler(isInFavorites = true, bookmarkId = bookmarkId)
          }
        }
      }
    }
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

  // --------------- HTTP Requests --------------- \\

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
        if (!response.isSuccessful)
          handleError("There was a problem getting pattern details") {
            requestPatternDetails(id, onSuccess)
          }
        else
          onSuccess(body!!, urlIsPdf)
      }
    }
  }

  fun addToLibrary(id: Long, onSuccess: (Map<String, Any>) -> Unit) {
    doAsync {
      val response = ravelryApi.addToLibrary(Volume(patternId = id)).execute()

      uiThread {
        if (!response.isSuccessful)
          handleError("There was a problem adding to library") {
            addToLibrary(id, onSuccess)
          }
        else
          onSuccess(response.body())
      }
    }
  }

  fun removeFromLibrary(patternName: String, onSuccess: (Map<String, Any>) -> Unit) {
    fun handleError() = handleError("There was a problem removing from library") {
      removeFromLibrary(patternName, onSuccess)
    }

    doAsync {
      val username = UserManager.getUsername()
      val librarySearchResponse = ravelryApi.searchLibrary(username, patternName).execute()

      if (!librarySearchResponse.isSuccessful)
        uiThread { handleError() }
      else if (librarySearchResponse.body().paginator.pageCount == 0)
        handleError()
      else {
        val volumeIdForPattern = librarySearchResponse.body().volumes[0].id
        val response = ravelryApi.removeFromLibrary(volumeIdForPattern).execute()

        uiThread {
          if (!response.isSuccessful) handleError()
          else onSuccess(response.body())
        }
      }
    }
  }

  fun addToQueue(patternId: Long, onSuccess: (Map<String, Any>) -> Unit) {
    doAsync {
      val username = UserManager.getUsername()
      val response = ravelryApi.addToQueue(username, QueuedProject(patternId)).execute()

      uiThread {
        if (!response.isSuccessful)
          handleError("There was a problem adding to queue") {
            addToQueue(patternId, onSuccess)
          }
        else
          onSuccess(response.body())
      }
    }
  }

  fun removeFromQueue(patternId: Long, onSuccess: (Map<String, Any>) -> Unit) {
    fun handleError() = handleError("There was a problem removing from queue") {
      removeFromQueue(patternId, onSuccess)
    }

    doAsync {
      val username = UserManager.getUsername()
      val queueSearchResponse = ravelryApi.getQueue(username, patternId).execute()

      if (!queueSearchResponse.isSuccessful)
        uiThread { handleError() }
      else if (queueSearchResponse.body().paginator.pageCount == 0)
        handleError()
      else {
        val queuedProjectId = queueSearchResponse.body().queuedProjects[0].id
        val response = ravelryApi.removeFromQueue(username, queuedProjectId).execute()

        uiThread {
          if (!response.isSuccessful) handleError()
          else onSuccess(response.body())
        }
      }
    }
  }

  fun addToFavorites(patternId: Long, onSuccess: (Long) -> Unit) {
    doAsync {
      val username = UserManager.getUsername()
      val response = ravelryApi.addToFavorites(username, Bookmark(patternId)).execute()

      uiThread {
        if (!response.isSuccessful)
          handleError("There was a problem adding to favorites") {
            addToFavorites(patternId, onSuccess)
          }
        else
          onSuccess(response.body().bookmark.id)
      }
    }
  }

  fun removeFromFavorites(bookmarkId: Long, onSuccess: (Map<String, Any>) -> Unit) {
    doAsync {
      val username = UserManager.getUsername()
      val response = ravelryApi.removeFromFavorites(username, bookmarkId).execute()

      uiThread {
        if (!response.isSuccessful)
          handleError("There was a problem removing from favorites") {
            removeFromFavorites(bookmarkId, onSuccess)
          }
        else
          onSuccess(response.body())
      }
    }
  }

  fun handleError(snackbarMessage: String, retryAction: () -> Unit) =
      Snackbar.make(container, snackbarMessage, LENGTH_LONG)
          .setAction("Retry") { retryAction() }
          .setDuration(3000)
          .show()

  // --------------- Non-fab Button Click Handlers --------------- \\

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

  // --------------- Permissions Request Lifecycle --------------- \\

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when (requestCode) {
      MY_PERMISSIONS_REQUEST_WRITE_EXT_STORAGE -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          downloadFile(this.patternDetails!!.downloadUrl, this.patternDetails!!.patternName)
        }
      }
    }
  }

  // --------------- Menu Item Click Handlers --------------- \\

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    android.R.id.home -> {
      onBackPressed()
      true
    }
    else -> super.onOptionsItemSelected(item)
  }
}