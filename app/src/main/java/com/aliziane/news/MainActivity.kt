package com.aliziane.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.aliziane.news.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as NyTimesApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment).navController
        binding.bottomNavigation.setupWithNavController(navController)
    }
}