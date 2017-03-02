package co.kenrg.yarnover.ext

import android.text.TextUtils
import android.widget.ImageView
import co.kenrg.yarnover.R
import com.squareup.picasso.Picasso

fun ImageView.loadImg(imageUrl: String) {
  if (TextUtils.isEmpty(imageUrl)) {
    Picasso.with(context).load(R.mipmap.ic_launcher).into(this)
  } else {
    Picasso.with(context).load(imageUrl).into(this)
  }
}
