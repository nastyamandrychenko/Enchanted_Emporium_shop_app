package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.AdressFragmentBinding

class AdressFragm : Fragment(R.layout.adress_fragment) {
    private lateinit var binding: AdressFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AdressFragmentBinding.bind(view)

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}