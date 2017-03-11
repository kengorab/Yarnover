package co.kenrg.yarnover.ext

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

fun Context.downloadFile(url: String, fileName: String) {
  val request = DownloadManager.Request(Uri.parse(url))
  request.setTitle(fileName)
  request.allowScanningByMediaScanner()
  request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
  request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$fileName.pdf")

  val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
  manager.enqueue(request)
}
