package com.example.gencont_app.formulaire

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gencont_app.R
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FormulaireActivity : AppCompatActivity() {

    private lateinit var viewFlipper: ViewFlipper

    // Step 1 UI elements
    private lateinit var courseTitleInputLayout: TextInputLayout
    private lateinit var courseTitleEditText: EditText
    private lateinit var proficiencyLevelSpinner: Spinner
    private lateinit var languageSpinner: Spinner
    private lateinit var descriptionInputLayout: TextInputLayout
    private lateinit var descriptionEditText: EditText
    private lateinit var nextButton: Button

    // Step 2 UI elements (Image Upload)
    private lateinit var uploadButton: Button
    private lateinit var imagePreview: ImageView
    private lateinit var generateButton: Button
    private lateinit var previousButton: Button

    private var selectedImageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1000

    // Initialize UI components
    private fun initializeUI() {
        viewFlipper = findViewById(R.id.viewFlipper)

        // Step 1
        courseTitleInputLayout = findViewById(R.id.textInputLayoutCourseTitle)
        courseTitleEditText = findViewById(R.id.editTextCourseTitle)
        proficiencyLevelSpinner = findViewById(R.id.spinnerProficiencyLevel)
        languageSpinner = findViewById(R.id.spinnerLanguage)
        descriptionInputLayout = findViewById(R.id.textInputLayoutDescription)
        descriptionEditText = findViewById(R.id.editTextDescription)
        nextButton = findViewById(R.id.buttonNext)

        // Step 2 (Image Upload)
        uploadButton = findViewById(R.id.uploadButton)
        imagePreview = findViewById(R.id.imagePreview)
        generateButton = findViewById(R.id.generateButton)
        previousButton = findViewById(R.id.buttonPrevious)
    }

    // Set up button click listeners
    private fun setupClickListeners() {
        nextButton.setOnClickListener {
            if (validateStep1()) {
                viewFlipper.showNext()
            }
        }

        previousButton.setOnClickListener {
            viewFlipper.showPrevious()
        }

        uploadButton.setOnClickListener {
            pickImageFromGallery()
        }

        generateButton.setOnClickListener {
            // Handle image generation logic here
            if (selectedImageUri != null) {
                generateContent()
            } else {
                Toast.makeText(this, "Please select an image first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Validate Step 1 inputs
    private fun validateStep1(): Boolean {
        var isValid = true

        if (courseTitleEditText.text.isNullOrBlank()) {
            courseTitleInputLayout.error = "Please enter a course title"
            isValid = false
        } else {
            courseTitleInputLayout.error = null  // Clear error
        }

        if (descriptionEditText.text.isNullOrBlank()) {
            descriptionInputLayout.error = "Please enter a description"
            isValid = false
        } else {
            descriptionInputLayout.error = null
        }

        return isValid
    }

    // Pick image from gallery
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // Handle result from image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            selectedImageUri = data?.data
            imagePreview.setImageURI(selectedImageUri)
            imagePreview.visibility = View.VISIBLE
        }
    }

    // Handle content generation and emotion detection
    private fun generateContent() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show()
            android.util.Log.d("FormulaireActivity", "No image selected.")
            return
        }

        android.util.Log.d("FormulaireActivity", "Image URI: $selectedImageUri")

        val imageFile = uriToFile(selectedImageUri!!)
        android.util.Log.d("FormulaireActivity", "Converted URI to file: ${imageFile.absolutePath}")

        detectEmotion(imageFile) { result ->
            runOnUiThread {
                android.util.Log.d("FormulaireActivity", "Emotion detection response: $result")

                if (result == null) {
                    Toast.makeText(this, "Error detecting emotion.", Toast.LENGTH_SHORT).show()
                    android.util.Log.d("FormulaireActivity", "API returned null response.")
                } else {
                    try {
                        val jsonObject = JSONObject(result)
                        val status = jsonObject.getString("status")
                        android.util.Log.d("FormulaireActivity", "Status: $status")

                        if (jsonObject.has("faces")) {
                            val facesArray = jsonObject.getJSONArray("faces")
                            android.util.Log.d("FormulaireActivity", "Found ${facesArray.length()} face(s)")

                            if (facesArray.length() > 0) {
                                val firstFace = facesArray.getJSONObject(0)
                                val emotion = firstFace.getString("dominant_emotion")
                                Toast.makeText(this, "Detected emotion: $emotion", Toast.LENGTH_LONG).show()
                                android.util.Log.d("FormulaireActivity", "Detected emotion: $emotion")
                            } else {
                                Toast.makeText(this, "No face/emotion detected.", Toast.LENGTH_SHORT).show()
                                android.util.Log.d("FormulaireActivity", "No faces found in response.")
                            }
                        } else {
                            Toast.makeText(this, "No emotion data returned.", Toast.LENGTH_SHORT).show()
                            android.util.Log.d("FormulaireActivity", "No 'faces' key in response.")
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Failed to parse response.", Toast.LENGTH_SHORT).show()
                        android.util.Log.e("FormulaireActivity", "JSON parsing error: ${e.message}")
                    }
                }
            }
        }
    }


    // Convert URI to File
    private fun uriToFile(uri: Uri): File {
        val fileName = getFileNameFromUri(uri)
        val tempFile = File.createTempFile("temp", fileName, cacheDir)
        contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(tempFile).use { output ->
                inputStream?.copyTo(output)
            }
        }
        return tempFile
    }

    // Get file name from URI
    private fun getFileNameFromUri(uri: Uri): String {
        var result = "temp_${System.currentTimeMillis()}.jpg"
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    result = cursor.getString(nameIndex)
                } else {
                    android.util.Log.w("FormulaireActivity", "DISPLAY_NAME not found in cursor columns.")
                }
            }
        } else {
            uri.lastPathSegment?.let {
                result = File(it).name
            }
        }

        android.util.Log.d("FormulaireActivity", "Resolved file name: $result")
        return result
    }


    // Detect emotion from image file via API
    private fun detectEmotion(imageFile: File, onResult: (String?) -> Unit) {
        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "photo", imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url("https://api.luxand.cloud/photo/emotions")
            .addHeader("token", "850045b7add54d8690f84d5070987bd7") // Replace with actual API key
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                onResult(result)
            }
        })
    }

    // onCreate to initialize everything
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulaire)
        initializeUI()
        setupClickListeners()
    }
}
