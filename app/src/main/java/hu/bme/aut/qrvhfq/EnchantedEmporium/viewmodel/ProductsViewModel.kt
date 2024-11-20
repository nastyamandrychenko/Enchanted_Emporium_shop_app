package hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel()  {


}