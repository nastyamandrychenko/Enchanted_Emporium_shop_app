package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
        cartProductAdapter.onProductClick = {
            val b = Bundle().apply {
                putParcelable("product", it.product)
            }
            findNavController().navigate(R.id.action_cartFragm_to_productDetailsFragment, b)
        }
        lifecycleScope.launchWhenStarted {
            cartViewModel.cartProducts.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarCart.visibility = View.GONE
                        val cartProducts = resource.data
                        cartProductAdapter.differ.submitList(cartProducts)

                        // Show or hide views based on whether the cart is empty
                        if (cartProducts.isNullOrEmpty()) {
                            binding.rvCart.visibility = View.GONE
                            binding.imageEmptyBoxTexture.visibility = View.VISIBLE
                        } else {
                            binding.rvCart.visibility = View.VISIBLE
                            binding.imageEmptyBoxTexture.visibility = View.GONE
                        }
                    }

                    is Resource.Error -> {
                        binding.progressbarCart.visibility = View.GONE
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



        lifecycleScope.launchWhenStarted {
            cartViewModel.deleteDialog.collect { cartProduct ->
                cartViewModel.deleteCartProduct(cartProduct)
            }
        }
    }
}