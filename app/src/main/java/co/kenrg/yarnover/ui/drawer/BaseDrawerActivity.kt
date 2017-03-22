package co.kenrg.yarnover.ui.drawer

import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import co.kenrg.yarnover.R
import co.kenrg.yarnover.facets.hotrightnow.HotRightNowActivity
import co.kenrg.yarnover.facets.userdetails.UserDetailsActivity
import co.kenrg.yarnover.oauth.UserManager
import kotlinx.android.synthetic.main.nav_header.*

open class BaseDrawerActivity : AppCompatActivity() {
  private lateinit var drawerToggle: ActionBarDrawerToggle

  protected fun setupDrawer(
      drawerLayout: DrawerLayout,
      toolbar: Toolbar,
      drawerNavigation: NavigationView
  ) {
    drawerToggle = object : ActionBarDrawerToggle(
        this,
        drawerLayout,
        toolbar,
        R.string.app_name,
        R.string.app_name
    ) {
      override fun onDrawerOpened(drawerView: View?) {
        super.onDrawerOpened(drawerView)

        username.text = UserManager.getUsername()
      }
    }

    drawerToggle.syncState()
    drawerLayout.addDrawerListener(drawerToggle)

    drawerNavigation.setNavigationItemSelectedListener { menuItem ->
      drawerLayout.closeDrawers()
      when (menuItem.itemId) {
        R.id.navHotRightNow -> startActivity(HotRightNowActivity::class.java)
        R.id.navFavorites,
        R.id.navLibrary,
        R.id.navQueue -> startActivity(UserDetailsActivity::class.java)
      }
      true
    }
  }

  private fun startActivity(activityClass: Class<*>) {
    startActivity(Intent(this, activityClass))
    finish()
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}