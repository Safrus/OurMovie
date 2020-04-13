package com.example.ourmovie.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.PagerAdapter
import com.example.ourmovie.viewpagers.SlidePagerAdapter
import com.example.ourmovie.viewpagers.CustomViewPager
import com.example.ourmovie.fragments.FavoriteMoviesFragment
import com.example.ourmovie.fragments.FilmsFragment
import com.example.ourmovie.fragments.ProfileFragment
import com.example.ourmovie.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MovieAppActivity: AppCompatActivity() {

    private var filmsFragment: Fragment = FilmsFragment()
    private var favoriteMoviesFragment: Fragment = FavoriteMoviesFragment()
    private var profileFragment: Fragment = ProfileFragment()
    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast

    private var fragmentList: MutableList<Fragment> = ArrayList()

    var fragmentManager: FragmentManager? = null
    var transaction: FragmentTransaction? = null

    private lateinit var pager: CustomViewPager
    var pagerAdapter: PagerAdapter? = null

    private val navListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(@NonNull menuItem: MenuItem): Boolean {
                transaction = fragmentManager?.beginTransaction()
                pagerAdapter?.notifyDataSetChanged()
                when (menuItem.getItemId()) {
                    R.id.nav_films -> pager.setCurrentItem(0, false)
                    R.id.nav_favorites -> pager?.setCurrentItem(1, false)
                    R.id.nav_profile -> pager?.setCurrentItem(2, false)
                }
                return false
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_app)
        fragmentManager = supportFragmentManager

        var bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnNavigationItemSelectedListener(navListener)

        fragmentList.add(filmsFragment)
        fragmentList.add(favoriteMoviesFragment)
        fragmentList.add(profileFragment)

        pager = findViewById(R.id.container)
        pager.setSwipable(false)
        pagerAdapter = SlidePagerAdapter(supportFragmentManager, fragmentList)
        pager.adapter = pagerAdapter
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel()
            super.onBackPressed()
            return
        } else {
            backToast = Toast.makeText(this@MovieAppActivity, "Press back again to exit", Toast.LENGTH_SHORT)
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}
