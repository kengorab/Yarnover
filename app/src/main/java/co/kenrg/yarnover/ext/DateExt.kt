package co.kenrg.yarnover.ext

import co.kenrg.yarnover.utils.DateUtils.displayFormatStr
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Date.format(formatStr: String = displayFormatStr): String? {
  try {
    val dateFormat = SimpleDateFormat(formatStr, Locale.getDefault())
    val date = Date()
    return dateFormat.format(date)
  } catch (e: ParseException) {
    e.printStackTrace()
    return null
  }
}