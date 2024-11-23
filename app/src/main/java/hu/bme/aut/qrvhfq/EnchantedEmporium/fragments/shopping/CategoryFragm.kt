package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.adapters.CategoriesAdapter
import hu.bme.aut.qrvhfq.EnchantedEmporium.adapters.ProductsAdapter
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.Category
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.CategoriesViewModel
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.ProductsViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.FragmentCategoryBinding

@AndroidEntryPoint
class CategoryFragm : Fragment(R.layout.fragment_category) {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var productsAdapter: ProductsAdapter
    private val viewModel: CategoriesViewModel by viewModels()
    private val productsViewModel: ProductsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCategoryBinding.bind(view)

        setupCategoryRecyclerView()
        setupProductRecyclerView()

        // Observe categories
        lifecycleScope.launchWhenStarted {
            viewModel.categories.collect { resource ->
                when (resource) {
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> {
                        hideLoading()
                        categoriesAdapter.setCategories(resource.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // Trigger category fetch
        viewModel.fetchCategories()
    }

    private fun setupCategoryRecyclerView() {
        categoriesAdapter = CategoriesAdapter { category ->
            navigateToCategoryProducts(category)
        }
        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = categoriesAdapter
        }
    }

    private fun setupProductRecyclerView() {
        productsAdapter = ProductsAdapter()
        productsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_categoryFragm_to_productDetailsFragment, bundle)
        }
        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productsAdapter
        }
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.GONE
    }

    private fun navigateToCategoryProducts(category: Category) {
        // Update the background of the category (change color, image, etc.)
//        binding.root.setBackgroundResource(R.drawable.favourite) // Example
        binding.productNameTextView.text = category.name
        // Fetch products for the selected category
        fetchProducts(category.name)

        // Optionally, change category text color or background

    }

    private fun fetchProducts(categoryName: String) {
        lifecycleScope.launchWhenStarted {
            productsViewModel.getProductsByCategory(categoryName).collect { resource ->
                when (resource) {
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> {
                        hideLoading()
                        productsAdapter.differ.submitList(resource.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}