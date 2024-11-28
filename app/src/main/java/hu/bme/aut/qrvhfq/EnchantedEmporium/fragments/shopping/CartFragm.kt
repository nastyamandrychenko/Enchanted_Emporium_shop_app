package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.qrvhfq.EnchantedEmporium.adapters.CartProductAdapter
import hu.bme.aut.qrvhfq.EnchantedEmporium.firebase.FirebaseCommon
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.CartViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.CartFragmentBinding

class CartFragm : Fragment(R.layout.cart_fragment) {
    private lateinit var binding: CartFragmentBinding
    private val cartViewModel: CartViewModel by activityViewModels() // Assuming CartViewModel is shared across fragments

    private val cartProductAdapter = CartProductAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CartFragmentBinding.bind(view)

        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = cartProductAdapter

        lifecycleScope.launchWhenStarted {
            cartViewModel.cartProducts.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        val cartProducts = resource.data
                        cartProductAdapter.differ.submitList(cartProducts)

                        binding.imageEmptyBoxTexture.visibility = if (cartProducts.isNullOrEmpty()) View.VISIBLE else View.GONE
                    }
                    is Resource.Error -> {
                    }
                    else -> Unit
                }
            }
        }

        cartProductAdapter.onPlusClick = { cartProduct ->
            cartViewModel.changeQuantity(cartProduct, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartProductAdapter.onMinusClick = { cartProduct ->
            cartViewModel.changeQuantity(cartProduct, FirebaseCommon.QuantityChanging.DECREASE)
        }

        cartProductAdapter.onProductClick = { cartProduct ->
        }

        lifecycleScope.launchWhenStarted {
            cartViewModel.deleteDialog.collect { cartProduct ->
                cartViewModel.deleteCartProduct(cartProduct)
            }
        }
    }
}