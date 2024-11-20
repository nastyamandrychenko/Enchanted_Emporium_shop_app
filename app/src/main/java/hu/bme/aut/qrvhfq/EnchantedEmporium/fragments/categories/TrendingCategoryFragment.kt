package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.categories

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.adapters.ProductsAdapter
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.ProductsViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.FragmentTrendingCategoryBinding
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TrendingCategoryFragment : Fragment(R.layout.fragment_trending_category) {
    private lateinit var binding: FragmentTrendingCategoryBinding
    private lateinit var productsAdapter: ProductsAdapter
    private val viewModel by viewModels<ProductsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrendingCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProductsRv()

        lifecycleScope.launchWhenStarted {
            viewModel.products.collectLatest {

                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        productsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE

    }

    private fun setupProductsRv() {
        productsAdapter = ProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2)
            adapter = productsAdapter
        }
    }
}