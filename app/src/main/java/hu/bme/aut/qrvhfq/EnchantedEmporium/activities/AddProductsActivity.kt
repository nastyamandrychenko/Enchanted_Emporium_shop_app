package hu.bme.aut.qrvhfq.EnchantedEmporium.activities

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import hu.bme.aut.qrvhfq.EnchantedEmporium.data.Product
import hu.bme.aut.qrvhfq.myapplication.databinding.ActivityAddProductsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddProductsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAddProductsBinding.inflate(layoutInflater) }
    private val selectedImages = mutableListOf<Uri>()
    private val storage = Firebase.storage.reference
    private val firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.SaveButton.setOnClickListener {
            if (validateInputs()) {
                saveProduct()
            }
        }

        val selectImageActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
                    intent?.clipData?.let { clipData ->
                        for (i in 0 until clipData.itemCount) {
                            clipData.getItemAt(i).uri?.let { selectedImages.add(it) }
                        }
                    } ?: intent?.data?.let { selectedImages.add(it) }

                    updateImagesUI()
                }
            }

        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            selectImageActivity.launch(intent)
        }

        binding.buttonClearImages.setOnClickListener {
            clearSelectedImages()
        }
    }

    private fun clearSelectedImages() {
        selectedImages.clear()
        updateImagesUI()
        Toast.makeText(this, "Images cleared", Toast.LENGTH_SHORT).show()
    }

    private fun updateImagesUI() {
        binding.tvSelectedImages.text = if (selectedImages.isEmpty()) {
            binding.buttonClearImages.isEnabled = false
            "No images selected"
        } else {
            binding.buttonClearImages.isEnabled = true
            "Selected images: ${selectedImages.size}"
        }
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun validateInputs(): Boolean {
        val requiredFields = listOf(
            binding.edName to "Name is required",
            binding.edCategory to "Category is required",
            binding.edPricn to "Price is required",
            binding.edDescription to "Description is required"
        )

        for ((field, errorMessage) in requiredFields) {
            if (field.text.toString().trim().isEmpty()) {
                field.error = errorMessage
                field.requestFocus()
                return false
            }
        }
        return true
    }

    private fun saveProduct() {
        val name = binding.edName.text.toString().trim()
        val category = binding.edCategory.text.toString().trim()
        val price = binding.edPricn.text.toString().trim().toFloatOrNull()
        val description = binding.edDescription.text.toString().trim()
        val imagesByteArrays = getImagesByteArrays()

        if (price == null) {
            binding.edPricn.error = "Price must be a valid number"
            binding.edPricn.requestFocus()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { showLoading() }

            val uploadedImages = mutableListOf<String>()
            for (imageBytes in imagesByteArrays) {
                val uploadUrl = uploadImage(imageBytes)
                uploadUrl?.let { uploadedImages.add(it) }
            }

            val product = Product(
                id = UUID.randomUUID().toString(),
                name = name,
                category = category,
                price = price,
                description = if (description.isEmpty()) null else description,
                images = uploadedImages
            )

            try {
                firestore.collection("Products").add(product).await()
                withContext(Dispatchers.Main) {
                    hideLoading()
                    Toast.makeText(this@AddProductsActivity, "Product saved successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("FirestoreError", "Error saving product: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    hideLoading()
                    Toast.makeText(this@AddProductsActivity, "Failed to save product.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun uploadImage(imageBytes: ByteArray): String? {
        val id = UUID.randomUUID().toString()
        return try {
            val imageRef = storage.child("products/images/$id")
            imageRef.putBytes(imageBytes).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("UploadError", "Error uploading image: ${e.message}", e)
            null
        }
    }

    private fun getImagesByteArrays(): List<ByteArray> {
        val byteArrays = mutableListOf<ByteArray>()
        selectedImages.forEach { uri ->
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                val outputStream = ByteArrayOutputStream()
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)) {
                    byteArrays.add(outputStream.toByteArray())
                }
            } catch (e: Exception) {
                Log.e("BitmapError", "Error processing image: $uri", e)
            }
        }
        return byteArrays
    }
}