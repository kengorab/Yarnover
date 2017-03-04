package co.kenrg.yarnover.facets.hotrightnow.adapter

import android.os.Parcel
import co.kenrg.yarnover.iface.adapter.DelegateViewItem
import co.kenrg.yarnover.iface.parcelable.DefaultParcelable
import co.kenrg.yarnover.iface.parcelable.read

sealed class ViewItem(viewType: ViewType) : DelegateViewItem<ViewItem.ViewType>(viewType) {
  enum class ViewType(val type: Int) {
    LOADING(0), PATTERN(1);

    companion object {
      fun byType(type: Int) = values().find { it.type == type } ?: LOADING
    }
  }

  object Loading : ViewItem(ViewType.LOADING)
  class Pattern(
      val id: Long,
      val patternName: String,
      val designerName: String,
      val photoUrl: String
  ) : ViewItem(ViewType.PATTERN), DefaultParcelable {
    companion object {
      @JvmField val CREATOR = DefaultParcelable.generateCreator {
        Pattern(it.read(), it.read(), it.read(), it.read())
      }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
      dest.writeLong(id)
      dest.writeString(patternName)
      dest.writeString(designerName)
      dest.writeString(photoUrl)
    }
  }
}