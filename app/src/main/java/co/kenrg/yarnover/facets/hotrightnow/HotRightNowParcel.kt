package co.kenrg.yarnover.facets.hotrightnow

import android.os.Parcel
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.iface.parcelable.DefaultParcelable
import co.kenrg.yarnover.iface.parcelable.read
import co.kenrg.yarnover.iface.parcelable.write

class HotRightNowParcel(val currentPage: Int, val patterns: List<ViewItem.Pattern>) : DefaultParcelable {
  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.write(currentPage, patterns)
  }

  companion object {
    @JvmField val CREATOR = DefaultParcelable.generateCreator {
      HotRightNowParcel(it.read(), it.read())
    }
  }
}