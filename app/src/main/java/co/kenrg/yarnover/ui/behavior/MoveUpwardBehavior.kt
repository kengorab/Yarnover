package co.kenrg.yarnover.ui.behavior

import android.content.Context
import android.os.Build
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View

class MoveUpwardBehavior(
    context: Context,
    attrs: AttributeSet
) : CoordinatorLayout.Behavior<View>(context, attrs) {
  companion object {
    private val SNACKBAR_BEHAVIOR_ENABLED = Build.VERSION.SDK_INT >= 11
  }

  override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
    return SNACKBAR_BEHAVIOR_ENABLED && dependency is Snackbar.SnackbarLayout
  }

  override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
    val translationY = Math.min(0f, (dependency.translationY - dependency.height))
    child.translationY = translationY
    return true
  }
}