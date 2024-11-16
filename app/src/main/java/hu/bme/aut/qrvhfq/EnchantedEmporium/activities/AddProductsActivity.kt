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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddProductsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAddProductsBinding.inflate(layoutInflater)}
    private var selectImages = mutableListOf<Uri>()
    private val storage = Firebase.storage.reference
    val firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.SaveButton.setOnClickListener {
            if (validateInputs()) {
                Toast.makeText(this, "Product saved successfully!", Toast.LENGTH_SHORT).show()
                saveProduct()
            }
        }

        val selectImageAct = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data

                if (intent?.clipData != null) {
                    val count = intent.clipData?.itemCount ?: 0
                    (0 until count).forEach {
                        val imagesUri = intent.clipData?.getItemAt(it)?.uri
                        imagesUri?.let { selectImages.add(it) }
                    }

                    //One images was selected
                } else {
                    val imageUri = intent?.data
                    imageUri?.let { selectImages.add(it) }
                }
                updateImages()
            }
        }

        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
          selectImageAct.launch((intent))
        }

    }
    private fun updateImages() {
        binding.tvSelectedImages.setText(selectImages.size.toString())
    }


    private fun showLoading() {
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun saveProduct() {
        val name = binding.edName.text.toString().trim()
        val category = binding.edCategory.text.toString().trim()
        val price = binding.edPricn.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()
        val imagesByteArrays = getImagesByteArrays()
        val images = mutableListOf<String>()

        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                showLoading()

            }
            try {
                async {
                    imagesByteArrays.forEach{
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imagesStorage = storage.child("products/images/$id")
                            val result = imagesStorage.putBytes(it).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }

                }.await()

            }catch (e: java.lang.Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    hideLoading()

                }
            }

            val product = Product(
                UUID.randomUUID().toString(),
                name,
                category,
                price.toFloat(),
                if (description.isEmpty()) null else description,
                images
            )

            firestore.collection("Products").add(product).addOnSuccessListener {
                hideLoading()
            }.addOnFailureListener {
                Log.e("error", it.message.toString())
                hideLoading()
            }

        }

    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectImages.forEach {
            val stream = ByteArrayOutputStream()
            val imageBmp = MediaStore.Images.Media.getBitmap(contentResolver, it)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 85, stream)) {
                val imageAsByteArray = stream.toByteArray()
                imagesByteArray.add(imageAsByteArray)
            }
        }
        return imagesByteArray
    }

    private fun getSizesList(sizesStr: String): List<String>? {
     if (sizesStr.isEmpty()){
         return null
     }
        val sizeList = sizesStr.split(",")
        return sizeList
    }

    private fun validateInputs(): Boolean {
        val requiredFields = listOf(
            binding.edName to "Name is required",
            binding.edCategory to "Category is required",
            binding.edDescription to "Description is required",
            binding.edPricn to "Price is required"
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
}