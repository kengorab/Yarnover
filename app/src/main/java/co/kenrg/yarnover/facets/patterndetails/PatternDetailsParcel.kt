package co.kenrg.yarnover.facets.patterndetails

import android.os.Parcel
import co.kenrg.yarnover.api.domain.PatternDetails
import co.kenrg.yarnover.iface.parcelable.DefaultParcelable

fun Parcel.writeBoolean(b: Boolean) {
  writeInt(if (b) 0 else 1)
}

fun Parcel.readBoolean(): Boolean {
  return readInt() == 0
}

class PatternDetailsParcel(
    val patternName: String,
    val patternAuthor: String,
    val downloadUrl: String,
    val urlIsPdf: Boolean,
    val craft: String,
    val categories: List<String>,
    val publishedDate: String,
    val patternSource: String?,
    val yarnWeightDesc: String,
    val gaugeDesc: String,
    val yardageDesc: String,
    val needleSizes: List<String>,
    val isInLibrary: Boolean,
    val isQueued: Boolean,
    val isFavorite: Boolean
) : DefaultParcelable {
  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeString(patternName)
    dest.writeString(patternAuthor)
    dest.writeString(downloadUrl)
    dest.writeBoolean(urlIsPdf)
    dest.writeString(craft)
    dest.writeStringList(categories)
    dest.writeString(publishedDate)
    dest.writeString(patternSource)
    dest.writeString(yarnWeightDesc)
    dest.writeString(gaugeDesc)
    dest.writeString(yardageDesc)
    dest.writeStringList(needleSizes)
    dest.writeBoolean(isInLibrary)
    dest.writeBoolean(isQueued)
    dest.writeBoolean(isFavorite)
  }

  constructor(patternDetails: PatternDetails, urlIsPdf: Boolean) : this(
      patternDetails.name,
      patternDetails.patternAuthor.name,
      patternDetails.downloadLocation.url,
      urlIsPdf,
      patternDetails.craft.name,
      patternDetails.patternCategories.map { it.name },
      patternDetails.published,
      if (patternDetails.printings.isNotEmpty()) patternDetails.printings[0].source.name else null,
      patternDetails.yarnWeightDesc,
      patternDetails.gaugeDesc,
      patternDetails.yardageDesc,
      patternDetails.patternNeedleSizes.map { it.name },
      patternDetails.personalAttributes.isInLibrary,
      patternDetails.personalAttributes.isQueued,
      patternDetails.personalAttributes.isFavorite
  )

  companion object {
    @JvmField val CREATOR = DefaultParcelable.generateCreator {
      PatternDetailsParcel(
          it.readString(),
          it.readString(),
          it.readString(),
          it.readInt() == 0,
          it.readString(),
          it.createStringArrayList(),
          it.readString(),
          it.readString(),
          it.readString(),
          it.readString(),
          it.readString(),
          it.createStringArrayList(),
          it.readBoolean(),
          it.readBoolean(),
          it.readBoolean()
      )
    }
  }
}