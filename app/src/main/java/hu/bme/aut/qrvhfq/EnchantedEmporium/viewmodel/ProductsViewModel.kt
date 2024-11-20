package hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.Product
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel()  {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val products: StateFlow<Resource<List<Product>>> = _products

    init {
        fetchProducts()

    }

    fun fetchProducts() {
        viewModelScope.launch {
            _products.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Trending now").get().addOnSuccessListener { result ->
                val productsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _products.emit(Resource.Success(productsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _products.emit(Resource.Error(it.message.toString()))
                }
            }
    }


}