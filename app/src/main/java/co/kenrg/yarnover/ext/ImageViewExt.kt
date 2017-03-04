package co.kenrg.yarnover.ext

import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.view.ViewTreeObserver
import android.widget.ImageView
import co.kenrg.yarnover.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

fun ImageView.loadImg(imageUrl: String, onSuccess: (ImageView) -> Unit = {}, onError: (ImageView) -> Unit = {}) {
  if (TextUtils.isEmpty(imageUrl)) {
    Picasso.with(context).load(R.mipmap.ic_launcher).into(this)
  } else {
    Picasso.with(context)
        .load(imageUrl)
        .noFade()
        .fit()
        .centerCrop()
        .error(R.color.lightGray)
        .placeholder(R.color.lightGray)
        .into(this, object : Callback {
          override fun onSuccess() {
            onSuccess(this@loadImg)
          }

          override fun onError() {
            onError(this@loadImg)
          }
        })
  }
}

fun ImageView.startPostponedTransition(activity: FragmentActivity) {
  this.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
    override fun onPreDraw(): Boolean {
      this@startPostponedTransition.viewTreeObserver.removeOnPreDrawListener(this)
      activity.supportStartPostponedEnterTransition()
      return true
    }
  })
}
