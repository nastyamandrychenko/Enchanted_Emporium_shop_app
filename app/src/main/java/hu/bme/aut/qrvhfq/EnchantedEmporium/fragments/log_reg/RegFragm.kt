package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.log_reg

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.User
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.RegisterValidation
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.validateEmail
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.validatePassword
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.RegisterViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.FragmentRegisterBinding

private val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegFragm: Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> ()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            RegisterBut.setOnClickListener {
                val email = EmailLabel.text.toString().trim()
                val password = PasswordLable.text.toString()

                // Validate email and password
                val emailValidation = validateEmail(email)
                val passwordValidation = validatePassword(password)

                // Handle email validation
                if (emailValidation is RegisterValidation.Failed) {
                    EmailLabel.apply {
                        hint = emailValidation.message
                        background = resources.getDrawable(R.drawable.edittext_border_red, null) // Change to red border
                    }
                } else {
                    EmailLabel.apply {
                        hint = "Enter your email"
                        background = resources.getDrawable(R.drawable.edittext_border, null) // Reset to default border
                    }
                }

                // Handle password validation
                if (passwordValidation is RegisterValidation.Failed) {
                    PasswordLable.apply {
                        hint = passwordValidation.message
                        background = resources.getDrawable(R.drawable.edittext_border_red, null) // Change to red border
                    }
                } else {
                    PasswordLable.apply {
                        hint = "Enter your password"
                        background = resources.getDrawable(R.drawable.edittext_border, null) // Reset to default border
                    }
                }

                // If both validations pass, proceed with registration
                if (emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success) {
                    val user = User(
                        firstNameLable.text.toString().trim(),
                        LastNameLable.text.toString().trim(),
                        email
                    )
                    viewModel.createAccountwithEmailandPassword(user, password)

                    lifecycleScope.launchWhenStarted {
                        viewModel.register.collect {
                            when (it) {
                                is Resource.Loading -> {
                                    RegisterBut.startAnimation()
                                }
                                is Resource.Success -> {
                                    Log.d("test", it.data.toString())
                                    RegisterBut.revertAnimation()
                                }
                                is Resource.Error -> {
                                    Log.d(TAG, it.message.toString())
                                    RegisterBut.revertAnimation()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}