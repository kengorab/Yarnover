package co.kenrg.yarnover

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Toast
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

  private fun makeLayout(mainActivity: MainActivity): View {
    val appLogoViewId = 1
    val primaryColor = getColor(mainActivity, R.color.colorPrimary)

    return mainActivity.UI {
      relativeLayout {
        lparams(width = matchParent, height = matchParent)
        backgroundColor = primaryColor

        verticalLayout {
          id = appLogoViewId

          textView("Yarnover") {
            gravity = Gravity.CENTER
            textColor = Color.WHITE
            textSize = 36f
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
          }

          imageView {
            setImageResource(R.drawable.yarn_ball)
            setColorFilter(Color.WHITE)
          }

          textView("for Ravelry") {
            gravity = Gravity.CENTER
            textColor = Color.WHITE
            textSize = 18f
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
          }

        }.lparams { centerInParent() }

        relativeLayout {

          button("Login to Ravelry") {
            textColor = primaryColor
            backgroundColor = Color.WHITE
            padding = dip(12)

            onClick {
              Toast.makeText(mainActivity, "Begin OAuth 1.0a process...", Toast.LENGTH_LONG).show()
            }
          }.lparams { centerInParent() }
        }.lparams {
          below(appLogoViewId)
          alignParentBottom()
          centerHorizontally()
        }
      }
    }.view
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(makeLayout(this))
  }
}
