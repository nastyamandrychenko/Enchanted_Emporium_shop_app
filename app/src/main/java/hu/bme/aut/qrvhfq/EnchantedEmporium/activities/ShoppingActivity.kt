package hu.bme.aut.qrvhfq.EnchantedEmporium.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.CartViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.ActivityShoppingBinding
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingBinding
    val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate binding and set the root as the content view
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the navigation controller only after the view is fully ready
        setupNavigation()
        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        val count = it.data?.size ?: 0
                        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomMenu)
                        bottomNavigation.getOrCreateBadge(R.id.cartFragm).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.dark_olive)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.hostFragm)
        binding.bottomMenu.setupWithNavController(navController)
    }

}