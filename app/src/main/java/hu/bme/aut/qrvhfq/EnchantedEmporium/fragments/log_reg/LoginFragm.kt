package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.log_reg

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.activities.AddProductsActivity
import hu.bme.aut.qrvhfq.EnchantedEmporium.activities.ShoppingActivity
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.LogInViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.FragmentLoginBinding


@AndroidEntryPoint
class LoginFragm: Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LogInViewModel>()

    private val validEmail = "admin@domain.com"
    private val validPassword = "admin123"

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

        binding.regIfNoLog.setOnClickListener {3
            findNavController().navigate(R.id.action_loginFragm_to_regFragm)
        }

        binding.apply {
            LogInBut.setOnClickListener {
                val email = logInEmail.text.toString().trim()
                val password = logInPassword.text.toString()

                val emailValidation = validateField(logInEmail, "Email cannot be empty")
                val passwordValidation = validateField(logInPassword, "Password cannot be empty")

                if (emailValidation && passwordValidation) {
                    if (email == validEmail && password == validPassword) {
                        // Navigate to AddProductsActivity if credentials match
                        Intent(requireActivity(), AddProductsActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    } else {
                        // Display error message for invalid credentials
                        Snackbar.make(requireView(), "Invalid login credentials", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> binding.LogInBut.startAnimation()
                    is Resource.Success -> {
                        binding.LogInBut.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                        binding.LogInBut.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        binding.logInEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetFieldBorder(binding.logInEmail)
        }
        binding.logInPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) resetFieldBorder(binding.logInPassword)
        }
    }

    private fun validateField(editText: EditText, errorMessage: String): Boolean {
        return if (editText.text.toString().isEmpty()) {
            editText.background = resources.getDrawable(R.drawable.edittext_border_red, null)
            Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    // Helper function to reset the field's border to default
    private fun resetFieldBorder(editText: EditText) {
        editText.background = resources.getDrawable(R.drawable.edittext_border, null)
    }
}