package hu.bme.aut.qrvhfq.EnchantedEmporium.fragments.shopping

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.User
import hu.bme.aut.qrvhfq.EnchantedEmporium.util.Resource
import hu.bme.aut.qrvhfq.EnchantedEmporium.viewmodel.AccountViewModel
import hu.bme.aut.qrvhfq.myapplication.R
@AndroidEntryPoint
class ProfileFragm : Fragment(R.layout.account_fragment){
    private lateinit var edFirstName: EditText
    private lateinit var edLastName: EditText
    private lateinit var edEmail: EditText
    private lateinit var imageUser: CircleImageView
    private lateinit var buttonSave: CircularProgressButton
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: AccountViewModel
    private val PICK_IMAGE_REQUEST = 1 // Request code for image picker
    private var imageUri: Uri? = null

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
        progressBar = view.findViewById(R.id.progressbarAccount)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]

        // Fetch current user data
        fetchUserData()

        // Handle Save button click
        buttonSave.setOnClickListener { saveChanges() }

        // Handle image click to pick a new image
        imageUser.setOnClickListener {
            openImagePicker()
        }

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
                            if (it.imagePatg.isNotEmpty()) {
                                Glide.with(requireContext())
                                    .load(it.imagePatg)  // This is the URL of the image
                                    .circleCrop()  // Optional: Crop the image into a circle
                                    .into(imageUser)
                            } else {
                                imageUser.setImageResource(R.drawable.test_image2)  // Set default image if URL is empty
                            }
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

        viewModel.updateUser(user, imageUri)
        observeUpdateStatus()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imageUser.setImageURI(imageUri)  // Display the selected image in the ImageView
        }
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