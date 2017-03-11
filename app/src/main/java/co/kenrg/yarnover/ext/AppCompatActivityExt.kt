package co.kenrg.yarnover.ext

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.checkPermission(permission: String, requestCode: Int, onIfGranted: () -> Unit) {
  if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
    ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
  else
    onIfGranted()
}

