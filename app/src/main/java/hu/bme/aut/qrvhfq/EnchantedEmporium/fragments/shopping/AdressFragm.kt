package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.AddressViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.AdressFragmentBinding

@AndroidEntryPoint
class AdressFragm : Fragment(R.layout.adress_fragment) {
    private lateinit var binding: AdressFragmentBinding
    private val addressViewModel: AddressViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AdressFragmentBinding.bind(view)

        loadAddress()

        binding.btnDelete.setOnClickListener{
            val street = ""
            val city = ""
            val country = ""
            val postalCode = ""
            val phone = ""
            val fullName = ""
            binding.btnDelete.startAnimation()


            addressViewModel.saveAddress(street, city, country, postalCode,phone,fullName,
                onSuccess = {
                    binding.etStreet.setText("")
                    binding.etCity.setText("")
                    binding.counrty.setText("")
                    binding.postalCode.setText("")
                    binding.etPhone.setText("")
                    binding.etFullName.setText("")
                    showToast("Address saved successfully!")
                    binding.btnDelete.revertAnimation()

                },
                onFailure = { exception ->
                    showToast("Failed to save address: ${exception.message}")
                    binding.btnDelete.revertAnimation()

                }
            )
        }

        binding.btnSave.setOnClickListener {
            val street = binding.etStreet.text.toString()
            val city = binding.etCity.text.toString()
            val country = binding.counrty.text.toString()
            val postalCode = binding.postalCode.text.toString()
            val phone = binding.etPhone.text.toString()
            val fullName = binding.etFullName.text.toString()
           binding.btnSave.startAnimation()


            addressViewModel.saveAddress(street, city, country, postalCode,phone,fullName,
                onSuccess = {

                    showToast("Address saved successfully!")
                    binding.btnSave.revertAnimation()

                },
                onFailure = { exception ->
                    showToast("Failed to save address: ${exception.message}")
                    binding.btnSave.revertAnimation()

                }
            )
        }

        binding.backArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun loadAddress() {
        addressViewModel.fetchAddress(
            onSuccess = { addressData ->
                binding.etStreet.setText(addressData["street"] ?: "")
                binding.etCity.setText(addressData["city"] ?: "")
                binding.counrty.setText(addressData["country"] ?: "")
                binding.postalCode.setText(addressData["postalCode"] ?: "")
                binding.etPhone.setText(addressData["phone"] ?: "")
                binding.etFullName.setText(addressData["full name"] ?: "")


            },
            onFailure = { exception ->
                showToast("Failed to load address: ${exception.message}")
            }
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}