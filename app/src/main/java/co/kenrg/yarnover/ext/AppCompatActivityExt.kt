package co.kenrg.yarnover.ext

import android.app.ActivityManager.TaskDescription
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import co.kenrg.yarnover.R

fun AppCompatActivity.checkPermission(permission: String, requestCode: Int, onIfGranted: () -> Unit) {
  if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
    ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
  else
    onIfGranted()
}

fun AppCompatActivity.setTaskDescription() {
  val launcherIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
  val taskDesc = TaskDescription(
      getString(R.string.app_name),
      launcherIcon,
      resources.getColor(R.color.colorPrimaryDark)
  )
  this.setTaskDescription(taskDesc)
}