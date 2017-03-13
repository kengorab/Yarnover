package co.kenrg.yarnover.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
  val parseFormatStr = "yyyy/MM/dd"
  val displayFormatStr = "MMMM d, yyyy"

  fun parse(string: String, format: String = parseFormatStr): Date? {
    try {
      val formatter = SimpleDateFormat(format, Locale.getDefault())
      return formatter.parse(string)
    } catch (e: ParseException) {
      e.printStackTrace()
      return null
    }
  }
}