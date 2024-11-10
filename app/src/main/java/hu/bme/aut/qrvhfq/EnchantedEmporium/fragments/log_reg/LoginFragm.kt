package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.log_reg

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.activities.ShoppingActivity
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.LogInViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.FragmentLoginBinding


@AndroidEntryPoint
class LoginFragm: Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LogInViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.regIfNoLog.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragm_to_regFragm)
        }
        binding.apply{
            LogInBut.setOnClickListener{
                val email = logInEmail.text.toString().trim()
                val password = logInPassword.text.toString()
                viewModel.login(email, password)

            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.login.collect{
                when (it) {
                    is Resource.Loading -> {
                        binding.LogInBut.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.LogInBut.revertAnimation()

                        Intent(requireActivity(), ShoppingActivity::class.java).also{
                            intent -> intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(),"Error: ${it.message}",Snackbar.LENGTH_LONG).show()
                        binding.LogInBut.revertAnimation()

                    }
                    else -> Unit

                }
            }
    }


    }
}
