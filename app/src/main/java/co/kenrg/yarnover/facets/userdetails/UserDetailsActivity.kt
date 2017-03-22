package co.kenrg.yarnover.facets.userdetails

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import co.kenrg.yarnover.R
import co.kenrg.yarnover.ext.setTaskDescription
import co.kenrg.yarnover.ui.drawer.BaseDrawerActivity
import kotlinx.android.synthetic.main.activity_userdetails.*
import java.util.*

class UserDetailsActivity : BaseDrawerActivity() {
  private val activity: UserDetailsActivity = this

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_userdetails)
    setTaskDescription()
    setupDrawer(drawerLayout, toolbar, drawerNavigation)

    toolbar.apply {
      activity.setSupportActionBar(this)
      activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
      activity.supportActionBar?.setHomeButtonEnabled(true)
    }

    setupViewPager(viewPager)
    tabs.setupWithViewPager(viewPager)
  }

  fun setupViewPager(viewPager: ViewPager) {
    val adapter = Adapter(supportFragmentManager)
    adapter.addFragment(BlankFragment(), "Favorites")
    adapter.addFragment(BlankFragment(), "Queue")
    adapter.addFragment(BlankFragment(), "Library")
    viewPager.adapter = adapter
  }

  internal class BlankFragment : Fragment()

  internal class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val mFragments = ArrayList<Fragment>()
    private val mFragmentTitles = ArrayList<String>()

    fun addFragment(fragment: Fragment, title: String) {
      mFragments.add(fragment)
      mFragmentTitles.add(title)
    }

    override fun getItem(position: Int): Fragment {
      return mFragments[position]
    }

    override fun getCount(): Int {
      return mFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
      return mFragmentTitles[position]
    }
  }
}