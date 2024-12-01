package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.qrvhfq.EnchantedEmporium.adapters.OrderDetailsAdapter
import hu.bme.aut.qrvhfq.myapplication.databinding.OrderDetailsBinding

class OrderDetailsFragm : Fragment() {
    private lateinit var binding: OrderDetailsBinding
    private val productsAdapter by lazy { OrderDetailsAdapter() }
    private val args by navArgs<OrderDetailsFragmArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OrderDetailsBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order

        setupOrderRv()

        binding.apply {

            tvOrderId.text = "Order #${order.orderId}"


            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone

            tvTotalPrice.text = "$ ${order.totalPrice}"

        }
        binding.imageCloseOrder.setOnClickListener {
            requireActivity().onBackPressed()
        }

        productsAdapter.differ.submitList(order.products)
    }

    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter = productsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        }
    }
}