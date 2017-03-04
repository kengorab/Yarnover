package co.kenrg.yarnover.facets.hotrightnow

import android.os.Parcel
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.iface.parcelable.DefaultParcelable

class HotRightNowParcel(val currentPage: Int, val patterns: List<ViewItem.Pattern>) : DefaultParcelable {
  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeInt(currentPage)
    dest.writeTypedList(patterns)
  }

  companion object {
    @JvmField val CREATOR = DefaultParcelable.generateCreator {
      HotRightNowParcel(it.readInt(), it.createTypedArrayList(ViewItem.Pattern.CREATOR))
    }
  }
}