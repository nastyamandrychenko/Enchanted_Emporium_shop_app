package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import de.hdodenhof.circleimageview.CircleImageView
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.User
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.AccountViewModel
import hu.bme.aut.qrvhfq.myapplication.R

class AccountFragment : Fragment() {

    private lateinit var edFirstName: EditText
    private lateinit var edLastName: EditText
    private lateinit var edEmail: EditText
    private lateinit var imageUser: CircleImageView
    private lateinit var buttonSave: CircularProgressButton
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.account_fragment, container, false)

        // Initialize UI elements
        edFirstName = view.findViewById(R.id.edFirstName)
        edLastName = view.findViewById(R.id.edLastName)
        edEmail = view.findViewById(R.id.edEmail)
        imageUser = view.findViewById(R.id.imageUser)
        buttonSave = view.findViewById(R.id.buttonSave)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]

        // Fetch current user data
        fetchUserData()

        // Handle Save button click
        buttonSave.setOnClickListener { saveChanges() }

        return view
    }

    private fun fetchUserData() {
        viewModel.getUser()
        lifecycleScope.launchWhenStarted {
            viewModel.user.collect { resource ->
                when (resource) {
                    is Resource.Loading -> progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        progressBar.visibility = View.GONE
                        val user = resource.data
                        user?.let {
                            edFirstName.setText(it.firstName)
                            edLastName.setText(it.lastName)
                            edEmail.setText(it.email)
                        }
                    }
                    is Resource.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun saveChanges() {
        val firstName = edFirstName.text.toString().trim()
        val lastName = edLastName.text.toString().trim()
        val email = edEmail.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            imagePatg = ""
        )

        viewModel.updateUser(user, null)
        observeUpdateStatus()
    }

    private fun observeUpdateStatus() {
        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        buttonSave.startMorphAnimation()
                    }
                    is Resource.Success -> {
                        progressBar.visibility = View.GONE
                        buttonSave.revertAnimation()
                        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        progressBar.visibility = View.GONE
                        buttonSave.revertAnimation()
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}