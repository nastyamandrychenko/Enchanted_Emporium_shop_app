package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.CartProduct
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.hideBottomNavigationView
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.DetailsViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.ProductDetailsBinding
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: ProductDetailsBinding
    private val viewModel by viewModels<DetailsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = ProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product
       binding.bachArrow.setOnClickListener{
           findNavController().navigateUp()
       }
        binding.buyNowButton.setOnClickListener{
            viewModel.addUpdateProductInCart(CartProduct(product, 1))
        }
        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.buyNowButton.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.buyNowButton.stopAnimation()
                        binding.buyNowButton.setBackgroundColor(R.drawable.added_to_cart_button)
                        Toast.makeText(requireContext(), "Product added to the cart", Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Error ->{
                        binding.buyNowButton.stopAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.apply {
            productTitle.text = product.name
            productPrice.text = "$ ${product.price.toInt()}"
            productDescription.text = product.description
        }
        loadImage(product.images.firstOrNull())
    }

    private fun loadImage(imagePath: String?) {
        if (!imagePath.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagePath)
                .into(binding.productImage)
        } else {
        }
    }
}