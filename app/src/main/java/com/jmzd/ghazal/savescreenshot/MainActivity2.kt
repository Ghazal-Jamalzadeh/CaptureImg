package com.jmzd.ghazal.savescreenshot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.jmzd.ghazal.savescreenshot.databinding.ActivityMain2Binding
import com.jmzd.ghazal.savescreenshot.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            navController = findNavController(R.id.navHost)

            appBarConfiguration = AppBarConfiguration(setOf(
                R.id.registerFragment,
                R.id.showReserveInfoFragment,
            ))
            setupActionBarWithNavController(navController , appBarConfiguration)

        }


    }
}