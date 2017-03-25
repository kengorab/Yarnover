package co.kenrg.yarnover.facets.userdetails

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.kenrg.yarnover.R
import co.kenrg.yarnover.ext.setTaskDescription
import co.kenrg.yarnover.facets.userdetails.tabs.FavoritesFragment
import co.kenrg.yarnover.ui.drawer.BaseDrawerActivity
import kotlinx.android.synthetic.main.activity_userdetails.*

class UserDetailsActivity : BaseDrawerActivity() {
  companion object {
    val KEY_TAB_ID = "TAB_ID"
    val TAB_INDEX_FAVORITES = 0
    val TAB_INDEX_QUEUE = 1
    val TAB_INDEX_LIBRARY = 2
  }

  private val activity: UserDetailsActivity = this

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_userdetails)
    setTaskDescription()
    setupDrawer(drawerLayout, toolbar, drawerNavigation)

    if (!intent.hasExtra(KEY_TAB_ID)) finish()
    val selectedTab = intent.getIntExtra(KEY_TAB_ID, 0)

    toolbar.apply {
      activity.setSupportActionBar(this)
      activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
      activity.supportActionBar?.setHomeButtonEnabled(true)
    }

    val tabItems = listOf(
        Triple("Favorites", R.drawable.ic_favorite, FavoritesFragment()),
        Triple("Queue", R.drawable.ic_queue, BlankFragment.newInstance("Queue")),
        Triple("Library", R.drawable.ic_library, BlankFragment.newInstance("Library"))
    )
    setupTabs(tabItems, selectedTab)
    activity.supportActionBar?.title = tabItems[selectedTab].first
  }

  fun setupTabs(tabItems: List<Triple<String, Int, Fragment>>, selectedTabIndex: Int) {
    val tabTitles = arrayListOf<String>()
    val adapter = PagerAdapter(supportFragmentManager)

    tabItems.forEach { (title, iconResId, fragment) ->
      val icon = resources.getDrawable(iconResId, theme)
      DrawableCompat.setTint(icon, Color.WHITE)
      val tab = tabs.newTab()
          .setIcon(icon)
          .setContentDescription(title)
      tabs.addTab(tab)
      adapter.addFragment(fragment)
      tabTitles.add(title)
    }
    tabs.getTabAt(selectedTabIndex)?.select()

    viewPager.adapter = adapter
    viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
    tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
    tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
      override fun onTabReselected(tab: TabLayout.Tab?) {}
      override fun onTabUnselected(tab: TabLayout.Tab?) {}

      override fun onTabSelected(tab: TabLayout.Tab?) {
        toolbar.title = tabTitles[tab?.position ?: 0]
      }
    })
  }

  internal class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fragments = arrayListOf<Fragment>()

    fun addFragment(fragment: Fragment) = fragments.add(fragment)

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size
  }

  internal class BlankFragment : Fragment() {
    companion object {
      fun newInstance(title: String): BlankFragment {
        val fragment = BlankFragment()
        val bundle = Bundle(1)
        bundle.putString("TITLE", title)
        fragment.arguments = bundle
        return fragment
      }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      val title = arguments.getString("TITLE")
      return TextView(activity).apply { text = title }
    }
  }
}