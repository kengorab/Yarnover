package co.kenrg.yarnover.iface.parcelable

import android.os.Parcel
import android.os.Parcelable

interface DefaultParcelable : Parcelable {
  override fun describeContents(): Int = 0

  companion object {
    fun <T> generateCreator(create: (source: Parcel) -> T) = object : Parcelable.Creator<T> {
      override fun createFromParcel(source: Parcel): T = create(source)
      override fun newArray(size: Int): Array<out T>? = newArray(size)
    }
  }
}

inline fun <reified T> Parcel.read(): T = readValue(T::class.javaClass.classLoader) as T
fun Parcel.write(vararg values: Any?) = values.forEach { writeValue(it) }
