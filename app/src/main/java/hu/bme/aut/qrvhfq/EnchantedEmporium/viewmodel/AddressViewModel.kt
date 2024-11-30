package hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    fun saveAddress(street: String, city: String, country: String, postalCode: String, phone: String,fullName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val addressData = hashMapOf(
            "street" to street,
            "city" to city,
            "country" to country,
            "postalCode" to postalCode,
            "phone" to phone,
            "full name" to fullName


        )
        firestore.collection("addresses").document(userId)
            .set(addressData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun fetchAddress(onSuccess: (Map<String, String>) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("addresses").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val addressData = document.data as? Map<String, String>
                    addressData?.let { onSuccess(it) }
                } else {
                    onSuccess(emptyMap())
                }
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}