package co.kenrg.yarnover.facets.patterndetails

import android.os.Parcel
import co.kenrg.yarnover.api.domain.PatternDetails
import co.kenrg.yarnover.iface.parcelable.DefaultParcelable

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
    val needleSizes: List<String>
) : DefaultParcelable {
  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeString(patternName)
    dest.writeString(patternAuthor)
    dest.writeString(downloadUrl)
    dest.writeInt(if (urlIsPdf) 0 else 1)
    dest.writeString(craft)
    dest.writeStringList(categories)
    dest.writeString(publishedDate)
    dest.writeString(patternSource)
    dest.writeString(yarnWeightDesc)
    dest.writeString(gaugeDesc)
    dest.writeString(yardageDesc)
    dest.writeStringList(needleSizes)
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
      patternDetails.patternNeedleSizes.map { it.name }
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
          it.createStringArrayList()
      )
    }
  }
}