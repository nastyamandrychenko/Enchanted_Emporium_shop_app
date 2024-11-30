package hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
) : ViewModel() {

    private val _products = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val products: StateFlow<Resource<List<Product>>> = _products
    private val _filteredProducts = MutableLiveData<Resource<List<Product>>>()
    val filteredProducts: LiveData<Resource<List<Product>>> = _filteredProducts

    private val _allProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val allProducts: StateFlow<Resource<List<Product>>> = _allProducts
    fun getProductsByCategory(category: String): StateFlow<Resource<List<Product>>> {
        viewModelScope.launch {
            _products.emit(Resource.Loading())
            firestore.collection("Products")
                .whereEqualTo("category", category).get()
                .addOnSuccessListener { result ->
                    val productsList = result.toObjects(Product::class.java)
                        .filter { !it.images.isNullOrEmpty() } // Exclude items with null or empty images
                    viewModelScope.launch {
                        _products.emit(Resource.Success(productsList))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _products.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
        return _products
    }

    fun searchProductsByName(query: String) {
        if (query.isBlank()) {
            _filteredProducts.value = Resource.Success(emptyList()) // Clear products
            return
        }

        _filteredProducts.value = Resource.Loading()

        firestore.collection("Products")
            .get()
            .addOnSuccessListener { result ->
                val productsList = result.toObjects(Product::class.java)

                // Perform case-insensitive search and partial matching
                val filteredList = productsList.filter {
                    it.name?.lowercase()?.contains(query.lowercase()) == true
                }

                _filteredProducts.value = Resource.Success(filteredList)
            }
            .addOnFailureListener { e ->
                _filteredProducts.value = Resource.Error(e.message.toString())
            }
    }

   
}