package hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.Category
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class CategoriesViewModel@Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _categories = MutableStateFlow<Resource<List<Category>>>(Resource.Unspecified())
    val categories: StateFlow<Resource<List<Category>>> = _categories

    fun fetchCategories() {
        viewModelScope.launch {
            _categories.emit(Resource.Loading())
            try {
                val categories = withContext(Dispatchers.IO) {
                    val snapshot = firestore.collection("Categories").get().await()
                    snapshot.toObjects(Category::class.java)
                }
                _categories.emit(Resource.Success(categories))
            } catch (e: Exception) {
                _categories.emit(Resource.Error(e.message.toString()))
            }
        }
    }
}