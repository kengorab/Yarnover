package co.kenrg.yarnover.ext

import android.util.TypedValue
import android.view.View
import co.kenrg.yarnover.R

fun View.actionBarSize(): Int {
  val tv = TypedValue()
  if (context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
    return TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
  }
  return 0
}
