package hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.User
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Constants.USER_COLLECTION
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db : FirebaseFirestore
): ViewModel() {
    private val _register = MutableStateFlow<Resource<User>>(Resource.Loading())
   val register:Flow<Resource<User>> = _register

    fun createAccountwithEmailandPassword(user: User, password : String){
firebaseAuth.createUserWithEmailAndPassword(user.email, password)
    .addOnSuccessListener {
it.user?.let {
    saveUserInfo(it.uid, user)

}
    }.addOnFailureListener{
        _register.value = Resource.Error(it.message.toString())

    }
    }

    private fun saveUserInfo(userUid: String, user: User) {
db.collection(USER_COLLECTION)
    .document(userUid)
    .set(user)
    .addOnSuccessListener {
  _register.value = Resource.Success(user)
    }.addOnFailureListener{
        _register.value = Resource.Error(it.message.toString())

    }
    }
}