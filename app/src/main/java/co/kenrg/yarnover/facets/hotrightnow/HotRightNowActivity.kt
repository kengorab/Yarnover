package co.kenrg.yarnover.facets.hotrightnow

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_NEVER
import android.view.View
import co.kenrg.yarnover.R
import co.kenrg.yarnover.api.ApiManager.api
import co.kenrg.yarnover.facets.hotrightnow.adapter.PatternDelegatorAdapter
import co.kenrg.yarnover.facets.hotrightnow.adapter.ViewItem
import co.kenrg.yarnover.facets.patterndetails.PatternDetailsActivity
import co.kenrg.yarnover.facets.patterndetails.PatternDetailsActivity.Companion.KEY_PATTERN_DATA
import co.kenrg.yarnover.iface.adapter.InfiniteScrollListener
import co.kenrg.yarnover.oauth.OAuthManager
import co.kenrg.yarnover.oauth.SplashActivity
import co.kenrg.yarnover.oauth.UserManager
import kotlinx.android.synthetic.main.activity_hotrightnow.*
import kotlinx.android.synthetic.main.component_patterncard.view.*
import kotlinx.android.synthetic.main.nav_header.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class HotRightNowActivity : AppCompatActivity() {
  companion object {
    private val KEY_PARCEL = "HOT_RIGHT_NOW_ACTIVITY_PARCEL"
  }

  private val activity: HotRightNowActivity = this
  private val ravelryApi = api()
  private val patternsAdapter = PatternDelegatorAdapter(onPatternClick = { item, view ->
    this.handleSelectPattern(item, view)
  })

  private lateinit var drawerToggle: ActionBarDrawerToggle
  private var currentPage = 0

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    val patterns = patternsAdapter.getPatterns()
    if (patterns.isNotEmpty())
      outState?.putParcelable(KEY_PARCEL, HotRightNowParcel(currentPage, patterns))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_hotrightnow)

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

    toolbar.apply {
      activity.setSupportActionBar(this)
      activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
      activity.supportActionBar?.setHomeButtonEnabled(true)
    }

    patternList.apply {
      this.adapter = patternsAdapter

      val linearLayoutManager = LinearLayoutManager(context)
      this.layoutManager = linearLayoutManager

      this.clearOnScrollListeners()
      this.addOnScrollListener(InfiniteScrollListener(linearLayoutManager) {
        requestPatterns(currentPage + 1)
      })
    }

    if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PARCEL)) {
      val parcel = savedInstanceState.get(KEY_PARCEL) as HotRightNowParcel
      currentPage = parcel.currentPage
      patternsAdapter.replaceWithPatterns(parcel.patterns)
    } else {
      requestPatterns(currentPage + 1)
    }
  }

  fun requestPatterns(page: Int) {
    doAsync {
      val response = ravelryApi.searchPatterns(page = page).execute()

      uiThread {
        if (!response.isSuccessful) {
          Snackbar.make(container, "There was a problem fetching patterns", Snackbar.LENGTH_LONG)
              .setAction("Retry") { requestPatterns(page) }
              .setDuration(3000)
              .show()
        } else {
          currentPage = page
          val patternViewItems = response.body().patterns.map { (id, name, _, firstPhoto, designer) ->
            val photoUrl = firstPhoto.mediumUrl ?: firstPhoto.medium2Url ?: firstPhoto.squareUrl
            ViewItem.Pattern(id, name, designer.name, photoUrl)
          }
          patternsAdapter.addPatterns(patternViewItems)
        }
      }
    }
  }

  fun handleSelectPattern(pattern: ViewItem.Pattern, view: View) {
    val intent = Intent(this, PatternDetailsActivity::class.java)
    intent.putExtra(KEY_PATTERN_DATA, pattern)

    val bundle = getSharedElementTransitionBundle(view)
    if (bundle != null)
      startActivity(intent, bundle)
    else
      startActivity(intent)
  }

  private fun getSharedElementTransitionBundle(patternView: View): Bundle? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val decorView = window.decorView
      val statusBackground = decorView.findViewById(android.R.id.statusBarBackground)
      val statusBarPair = Pair.create<View, String>(statusBackground, statusBackground.transitionName)
      val navigation = decorView.findViewById(android.R.id.navigationBarBackground)
      val navigationPair = Pair.create<View, String>(navigation, navigation.transitionName)

      val pair = Pair.create(patternView.previewImage as View, getString(R.string.transition_preview_image))
      val animation = ActivityOptions.makeSceneTransitionAnimation(this, pair, statusBarPair, navigationPair)
      return animation.toBundle()
    } else {
      return null
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val logoutMenuItem = menu?.add("Logout")
    logoutMenuItem?.setShowAsAction(SHOW_AS_ACTION_NEVER)
    logoutMenuItem?.setOnMenuItemClickListener { handleLogout() }
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  fun handleLogout(): Boolean {
    OAuthManager.clearAccessToken()
    UserManager.clearUsernameFromSharedPrefs()
    startActivity(Intent(this, SplashActivity::class.java))
    finish()
    return true
  }
}
