package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.activities.LoginRegActivity
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.SharedPrefsUtil
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.AccountViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.FragmentProfileBinding

@AndroidEntryPoint
class ProfileFragm : Fragment(R.layout.fragment_profile){
    private lateinit var binding: FragmentProfileBinding
    private val accountViewModel: AccountViewModel by viewModels()
    private lateinit var sharedPrefsUtil: SharedPrefsUtil

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefsUtil = SharedPrefsUtil(requireContext())
        binding = FragmentProfileBinding.bind(view)
        accountViewModel.getUser()
        lifecycleScope.launchWhenStarted {
            accountViewModel.user.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {

                        val user = resource.data
                        user?.let {
                            binding.profileName.setText(it.firstName + " " + it.lastName)

                            if (it.imagePatg.isNotEmpty()) {
                                Glide.with(requireContext())
                                    .load(it.imagePatg)
                                    .circleCrop()
                                    .into(binding.profileImage)
                            } else {
                                binding.profileImage.setImageResource(R.drawable.test_image2)
                            }
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }


        binding.logoutSection.setOnClickListener {
            // Clear shared preferences
            sharedPrefsUtil.clearPreferences()

            // Restart the app
            val intent = Intent(requireActivity(), LoginRegActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish() // Ensure the current activity is completely destroyed
        }

        binding.trackOrders.setOnClickListener{

            findNavController().navigate(R.id.accountFragment)
        }

        binding.billing.setOnClickListener{

            findNavController().navigate(R.id.adressFragm)
        }

        binding.allOrders.setOnClickListener{

            findNavController().navigate(R.id.ordersFragm)
        }
    }


}