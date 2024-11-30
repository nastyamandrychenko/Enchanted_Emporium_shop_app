package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.adapters.ProductsAdapter
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.ProductsViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.FragmentTopLayoutBinding

@AndroidEntryPoint
class TopLayoutFragment : Fragment(R.layout.fragment_top_layout) {

    private lateinit var binding: FragmentTopLayoutBinding
    private lateinit var productsAdapter: ProductsAdapter
    private val viewModel by viewModels<ProductsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productsAdapter = ProductsAdapter()
        binding.searchRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productsAdapter
            visibility = View.GONE // Initially hide RecyclerView
        }

        // Handle product item click
        productsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragm_to_productDetailsFragment, bundle)
        }

        // Set up search bar actions
        binding.serchEditText.apply {
            setOnClickListener {
                binding.cross.visibility = View.GONE
                clearFocus()
                binding.searchRecyclerView.visibility = View.GONE
            }

            addTextChangedListener { text ->
                if (!text.isNullOrEmpty()) {
                    // Perform search with the current text
                    viewModel.searchProductsByName(text.toString())
                    binding.cross.visibility = View.VISIBLE
                    binding.searchRecyclerView.visibility = View.VISIBLE
                } else {
                    productsAdapter.submitList(emptyList())
                    binding.cross.visibility = View.GONE
                    binding.searchRecyclerView.visibility = View.GONE
                }
            }
        }

        binding.cross.setOnClickListener {
            binding.serchEditText.setText("")
            binding.cross.visibility = View.GONE
            productsAdapter.submitList(emptyList())
            binding.serchEditText.clearFocus()
            binding.searchRecyclerView.visibility = View.GONE
        }

        viewModel.filteredProducts.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    productsAdapter.differ.submitList(resource.data)
                    binding.searchRecyclerView.visibility = if (resource.data.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
                is Resource.Unspecified ->{}
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    binding.searchRecyclerView.visibility = View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.serchEditText.text.isNullOrEmpty()) {
            binding.searchRecyclerView.visibility = View.GONE
        } else {
            binding.searchRecyclerView.visibility = View.VISIBLE
        }
    }
}