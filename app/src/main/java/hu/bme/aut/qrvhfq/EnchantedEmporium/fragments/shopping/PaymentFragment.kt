package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.AddressViewModel
import hu.bme.aut.qrvhfq.myapplication.R
import hu.bme.aut.qrvhfq.myapplication.databinding.PaymentMethodBinding
import java.util.Calendar

@AndroidEntryPoint
class PaymentFragment : Fragment(R.layout.payment_method) {
    private lateinit var binding: PaymentMethodBinding
    private val addressViewModel: AddressViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PaymentMethodBinding.bind(view)
        loadPaymentMethod()

        binding.paymentMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.byCardRadioButton -> {
                    binding.cardFieldsLayout.visibility = View.VISIBLE
                }
                R.id.byCashRadioButton -> {
                    binding.cardFieldsLayout.visibility = View.GONE
                }
            }
        }
        binding.backArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            val paymentMethod = when (binding.paymentMethodGroup.checkedRadioButtonId) {
                R.id.byCardRadioButton -> "By Card"
                R.id.byCashRadioButton -> "By Cash"
                else -> return@setOnClickListener
            }
            binding.btnSave.startAnimation()
            if (paymentMethod == "By Card") {
                val cardNumber = binding.cardNumber.text.toString()
                val expiryDate = binding.cardExpiryDate.text.toString()
                val cvv = binding.cardCVV.text.toString()
                if (!validateCardInputs(cardNumber, expiryDate, cvv)) {
                    binding.btnSave.revertAnimation()
                    return@setOnClickListener
                }

                savePaymentMethod(paymentMethod, cardNumber, expiryDate, cvv)
            } else {
                if (paymentMethod.isEmpty()) {
                    showToast("Please select a payment method.")
                    return@setOnClickListener
                }
                savePaymentMethod(paymentMethod, "", "", "")
            }
        }

        binding.btnDelete.setOnClickListener {
            binding.btnDelete.startAnimation()
            clearFields()
            showToast("All fields have been cleared!")
        }
    }

    private fun validateCardInputs(cardNumber: String, expiryDate: String, cvv: String): Boolean {
        // Validate Card Number (must be numeric and between 13-19 digits)
        if (cardNumber.isEmpty() || !cardNumber.matches("\\d{13,19}".toRegex())) {
            showToast("Invalid card number. It should be between 13 and 19 digits.")
            return false
        }

        // Validate Expiry Date (must be in MM/YY format and in the future)
        if (expiryDate.isEmpty() || !expiryDate.matches("\\d{2}/\\d{2}".toRegex())) {
            showToast("Invalid expiry date. Please enter in MM/YY format.")
            return false
        }

        val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100 // Get last two digits of current year
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1 // Get current month

        val expiryMonth = expiryDate.substring(0, 2).toInt()
        val expiryYear = expiryDate.substring(3, 5).toInt()

        if (expiryYear < currentYear || (expiryYear == currentYear && expiryMonth < currentMonth)) {
            showToast("Expiry date must be in the future.")
            return false
        }

        // Validate CVV (must be a 3 or 4 digit number)
        if (cvv.isEmpty() || !cvv.matches("\\d{3,4}".toRegex())) {
            showToast("Invalid CVV. It should be 3 or 4 digits.")
            return false
        }

        return true
    }


    private fun savePaymentMethod(paymentMethod: String, cardNumber: String, expiryDate: String, cvv: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        binding.btnSave.revertAnimation()

        val paymentData = hashMapOf(
            "paymentMethod" to paymentMethod,
            "cardNumber" to cardNumber,
            "expiryDate" to expiryDate,
            "cvv" to cvv
        )

        FirebaseFirestore.getInstance().collection("users")
            .document(userId)
            .collection("payments")
            .document("paymentDetails")
            .set(paymentData)
            .addOnSuccessListener {
                showToast("Payment method saved successfully!")
            }
            .addOnFailureListener { exception ->
                showToast("Failed to save payment method: ${exception.message}")

            }


    }
    private fun loadPaymentMethod() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("users")
            .document(userId)
            .collection("payments")
            .document("paymentDetails")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val paymentMethod = document.getString("paymentMethod")
                    val cardNumber = document.getString("cardNumber")
                    val expiryDate = document.getString("expiryDate")
                    val cvv = document.getString("cvv")

                    if (paymentMethod == "By Card") {
                        binding.paymentMethodGroup.check(R.id.byCardRadioButton)
                        binding.cardFieldsLayout.visibility = View.VISIBLE
                        binding.cardNumber.setText(cardNumber)
                        binding.cardExpiryDate.setText(expiryDate)
                        binding.cardCVV.setText(cvv)
                    } else {
                        binding.paymentMethodGroup.check(R.id.byCashRadioButton)
                        binding.cardFieldsLayout.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener { exception ->
                showToast("Failed to load payment method: ${exception.message}")
            }
    }

    private fun clearFields() {
        binding.paymentMethodGroup.clearCheck()
        binding.cardNumber.text?.clear()
        binding.cardExpiryDate.text?.clear()
        binding.cardCVV.text?.clear()
        binding.btnDelete.revertAnimation()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}