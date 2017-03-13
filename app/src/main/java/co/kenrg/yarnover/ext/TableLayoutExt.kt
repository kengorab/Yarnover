package co.kenrg.yarnover.ext

import android.graphics.Typeface
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import org.jetbrains.anko.dip

fun TableLayout.addRow(col1: String, col2: String) =
    this.addView(TableRow(context).apply {
      layoutParams = TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
        setPadding(dip(4), dip(4), dip(4), dip(4))
      }

      addView(TextView(context).apply {
        text = col1
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        layoutParams = TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
          column = 1
          marginEnd = dip(16)
        }
      })

      addView(TextView(context).apply {
        text = col2
        maxLines = 2
        layoutParams = TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
          column = 2
        }
      })
    })
