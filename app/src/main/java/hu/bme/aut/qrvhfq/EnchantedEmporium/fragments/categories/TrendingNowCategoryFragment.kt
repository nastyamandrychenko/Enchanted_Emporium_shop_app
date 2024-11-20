package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.categories

import androidx.fragment.app.Fragment
import hu.bme.aut.qrvhfq.myapplication.R

//@AndroidEntryPoint
open class TrendingNowCategoryFragment : Fragment(R.layout.fragment_trending_category){
//    private lateinit var binding: FragmentTrendingCategoryBinding
//    private lateinit var productsAdapter: ProductsAdapter
//    private val viewModel by viewModels<ProductsViewModel>()
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentTrendingCategoryBinding.inflate(inflater)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//    setupProductsRv()
//
//        lifecycleScope.launchWhenStarted {
//            viewModel.products.collectLatest {
//
//                when(it){
//                    is Resource.Loading -> {
//                        showLoading()
//                    }
//                    is Resource.Success -> {
//                        productsAdapter.differ.submitList(it.data)
//                        hideLoading()
//                    }
//                    is Resource.Error -> {
//                        hideLoading()
//                        Log.e(TAG, it.message.toString())
//                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
//                    }
//                    else -> Unit
//                }
//            }
//        }
//    }
//
//    private fun hideLoading() {
//        binding.progressbar.visibility = View.GONE
//    }
//
//    private fun showLoading() {
//        binding.progressbar.visibility = View.VISIBLE
//
//    }
//
//    private fun setupProductsRv() {
//        productsAdapter = ProductsAdapter()
//        binding.rvSpecialProducts.apply {
//            layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//            adapter = productsAdapter
//        }
//    }
}