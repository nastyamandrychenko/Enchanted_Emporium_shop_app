package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.adapters.CartProductAdapter
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.Address
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.CartProduct
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.orders.Order
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.orders.OrderStatus
import hu.bme.aut.qrvhfq.EnchantedEmporium.firebase.FirebaseCommon
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.AddressViewModel
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.CartViewModel
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.OrderViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.CartFragmentBinding
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class CartFragm : Fragment(R.layout.cart_fragment) {
    private lateinit var binding: CartFragmentBinding
    private val cartViewModel: CartViewModel by activityViewModels()
    private var selectedAddress: Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()
    private val cartProductAdapter = CartProductAdapter()
    var totalPrice = 0f
    private var products = emptyList<CartProduct>()
    private val addressViewModel: AddressViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CartFragmentBinding.bind(view)
        products = cartViewModel.getCartProductsAsList()
        lifecycleScope.launchWhenStarted {
            cartViewModel.productsPrice.collectLatest { price ->
                price?.let {
                    totalPrice = it
                    calculateCartSummary(totalPrice)
                }
            }
        }
        fetchAndDisplayAddress()

        binding.buttonCheckout.setOnClickListener{
//            if (selectedAddress == null) {
//                Toast.makeText(requireContext(), "Please select and address", Toast.LENGTH_SHORT)
//                    .show()
//                return@setOnClickListener
//            }
            showOrderConfirmationDialog()

        }

        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = cartProductAdapter
        cartProductAdapter.onProductClick = {
            val b = Bundle().apply {
                putParcelable("product", it.product)
            }
            findNavController().navigate(R.id.action_cartFragm_to_productDetailsFragment, b)
        }
        binding.deliveryAddressContainer.setOnClickListener {
            findNavController().navigate(R.id.adressFragm)
        }
        binding.paymentMethodContainer.setOnClickListener{
            findNavController().navigate(R.id.paymentFragment)

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


        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonCheckout.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonCheckout.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(), "Your order was placed", Snackbar.LENGTH_LONG)
                            .show()
                    }

                    is Resource.Error -> {
                        binding.buttonCheckout.revertAnimation()
                        Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }

    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order items")
            setMessage("Do you want to order your cart items?")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes") { dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun fetchAndDisplayAddress() {
        addressViewModel.fetchAddress(
            onSuccess = { addressData ->
                if (addressData.isNotEmpty()) {
                    selectedAddress = Address(
                        street = addressData["street"] ?: "",
                        city = addressData["city"] ?: "",
                        country = addressData["country"] ?: "",
                        postalCode = addressData["postalCode"] ?: "",
                        phone = addressData["phone"] ?: "",
                        fullName = addressData["full name"] ?: ""
                    )

                }
            },
            onFailure = { exception ->
                Toast.makeText(requireContext(), "Error fetching address: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun calculateCartSummary(subtotal: Float) {
        val taxPercentage = 0.1

        val deliveryFee = when {
            subtotal > 400 -> 0.0
            subtotal > 200 -> subtotal * 0.05
            else -> subtotal * 0.1
        }

        val tax = subtotal * taxPercentage

        val total = subtotal + tax + deliveryFee
        totalPrice = total.toFloat()

        binding.apply {
            SubTotalPrice.text = "$${String.format("%.2f", subtotal)}"
            TotTaxPrice.text = "$${String.format("%.2f", tax)}"
            deliveryPrice.text = if (deliveryFee == 0.0) "Free" else "$${String.format("%.2f", deliveryFee)}"
            tvTotalPrice.text = "$${String.format("%.2f", total)}"
        }
    }
}