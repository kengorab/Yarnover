package co.kenrg.yarnover.facets.patterndetails

import android.os.Parcel
import co.kenrg.yarnover.api.domain.PatternDetails
import co.kenrg.yarnover.iface.parcelable.DefaultParcelable

class PatternDetailsParcel(
    val patternName: String,
    val patternAuthor: String,
    val downloadUrl: String,
    val urlIsPdf: Boolean
) : DefaultParcelable {
  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeString(patternName)
    dest.writeString(patternAuthor)
    dest.writeString(downloadUrl)
    dest.writeInt(if (urlIsPdf) 0 else 1)
  }

  constructor(patternDetails: PatternDetails, urlIsPdf: Boolean) : this(
      patternDetails.name,
      patternDetails.patternAuthor.name,
      patternDetails.downloadLocation.url,
      urlIsPdf
  )

  companion object {
    @JvmField val CREATOR = DefaultParcelable.generateCreator {
      PatternDetailsParcel(
          it.readString(),
          it.readString(),
          it.readString(),
          it.readInt() == 0
      )
    }
  }
}