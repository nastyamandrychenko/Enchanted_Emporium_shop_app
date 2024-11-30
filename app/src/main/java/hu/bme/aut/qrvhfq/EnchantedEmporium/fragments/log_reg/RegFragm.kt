package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.log_reg

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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


        binding.logIfNoReg.setOnClickListener {
            findNavController().navigate(R.id.action_regFragm_to_loginFragm)
        }

        binding.apply {
            RegisterBut.setOnClickListener {
                val firstName = firstNameLable.text.toString().trim()
                val lastName = LastNameLable.text.toString().trim()
                val email = EmailLabel.text.toString().trim()
                val password = PasswordLable.text.toString()

                // Validate First Name
                val firstNameValidation = if (firstName.isEmpty()) {
                    RegisterValidation.Failed("First name cannot be empty")
                } else {
                    RegisterValidation.Success
                }

                // Validate Last Name
                val lastNameValidation = if (lastName.isEmpty()) {
                    RegisterValidation.Failed("Last name cannot be empty")
                } else {
                    RegisterValidation.Success
                }

                val emailValidation = validateEmail(email)

                val passwordValidation = validatePassword(password)

                handleFieldValidation(firstNameLable, firstNameValidation)
                handleFieldValidation(LastNameLable, lastNameValidation)
                handleFieldValidation(EmailLabel, emailValidation)
                handleFieldValidation(PasswordLable, passwordValidation)

                if (firstNameValidation is RegisterValidation.Success &&
                    lastNameValidation is RegisterValidation.Success &&
                    emailValidation is RegisterValidation.Success &&
                    passwordValidation is RegisterValidation.Success
                ) {
                    val user = User(firstName, lastName, email)
                    viewModel.createAccountwithEmailandPassword(user, password)

                    lifecycleScope.launchWhenStarted {
                        viewModel.register.collect {
                            when (it) {
                                is Resource.Loading -> {
                                    RegisterBut.startAnimation()
                                }
                                is Resource.Success -> {
                                    findNavController().navigate(R.id.action_regFragm_to_loginFragm)
                                    RegisterBut.revertAnimation()
                                }
                                is Resource.Error -> {
                                    Log.d(TAG, it.message.toString())
                                    RegisterBut.revertAnimation()
                                }
                                else -> Unit
                            }
                        }
                    }
                }
            }

            // Reset error and background when user focuses on EditText
            firstNameLable.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    firstNameLable.background = resources.getDrawable(R.drawable.edittext_border, null) // Reset to default
                }
            }

            LastNameLable.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    LastNameLable.background = resources.getDrawable(R.drawable.edittext_border, null) // Reset to default
                }
            }

            EmailLabel.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    EmailLabel.background = resources.getDrawable(R.drawable.edittext_border, null) // Reset to default
                }
            }

            PasswordLable.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    PasswordLable.background = resources.getDrawable(R.drawable.edittext_border, null) // Reset to default
                }
            }
        }
    }

    private fun handleFieldValidation(editText: EditText, validation: RegisterValidation) {
        when (validation) {
            is RegisterValidation.Failed -> {
                editText.background = resources.getDrawable(R.drawable.edittext_border_red, null) // Red border
            }
            is RegisterValidation.Success -> {
                editText.background = resources.getDrawable(R.drawable.edittext_border, null) // Reset to default
            }
        }
    }
}