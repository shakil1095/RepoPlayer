/*
 * RepoPlayer - A privacy-respecting media center app for Android.
 * Copyright (C) 2026
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.repoplayer.ui

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.repoplayer.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> showHome()
                R.id.nav_repos -> showRepositories()
                R.id.nav_settings -> showSettings()
            }
            true
        }

        val initialFragmentId = intent.getStringExtra("fragment")
        when (initialFragmentId) {
            "repos" -> showRepositories()
            "settings" -> showSettings()
            else -> showHome()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            return super.onKeyDown(keyCode, event)
        }
        return super.onKeyDown(keyCode, event)
    }

    fun showHome() {
        replaceFragment(HomeFragment())
        bottomNavigation.selectedItemId = R.id.nav_home
    }

    fun showRepositories() {
        replaceFragment(RepositoriesFragment())
        bottomNavigation.selectedItemId = R.id.nav_repos
    }

    fun showSettings() {
        replaceFragment(SettingsFragment())
        bottomNavigation.selectedItemId = R.id.nav_settings
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commitAllowingStateLoss()
    }
}
