package co.kenrg.yarnover.ui.patternlist.adapter

import android.os.Parcel
import co.kenrg.yarnover.iface.parcelable.DefaultParcelable

class PatternListParcel(val currentPage: Int, val patterns: List<ViewItem.Pattern>) : DefaultParcelable {
  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeInt(currentPage)
    dest.writeTypedList(patterns)
  }

  companion object {
    @JvmField val CREATOR = DefaultParcelable.generateCreator {
      PatternListParcel(it.readInt(), it.createTypedArrayList(ViewItem.Pattern.Companion.CREATOR))
    }
  }
}