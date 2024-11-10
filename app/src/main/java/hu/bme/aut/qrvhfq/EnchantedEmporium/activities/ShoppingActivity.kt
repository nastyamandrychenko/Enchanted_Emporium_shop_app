package hu.bme.aut.qrvhfq.EnchantedEmporium.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.ActivityShoppingBinding

class ShoppingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate binding and set the root as the content view
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the navigation controller only after the view is fully ready
        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.hostFragm)
        binding.bottomMenu.setupWithNavController(navController)
    }
}