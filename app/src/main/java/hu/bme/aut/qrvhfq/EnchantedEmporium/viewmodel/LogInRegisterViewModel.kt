package hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogInRegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {
}