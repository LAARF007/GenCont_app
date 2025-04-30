package com.example.gencont_app.formulaire

import CoursePersister
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gencont_app.R
import com.example.gencont_app.api.ChatApiClient
import com.example.gencont_app.configDB.dao.PromptDao
import com.example.gencont_app.configDB.data.Prompt
import com.example.gencont_app.configDB.database.AppDatabase
import com.example.gencont_app.cours.CoursActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

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
    lateinit var etat_visage: String


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
            courseTitleInputLayout.error = null
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
            Log.d("FormulaireActivity", "No image selected.")
            return
        }

        // Show a progress indicator
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Processing image...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Move heavy operations to background thread
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("FormulaireActivity", "Image URI: $selectedImageUri")

                val imageFile = uriToFile(selectedImageUri!!)
                Log.d("FormulaireActivity", "Converted URI to file: ${imageFile.absolutePath}")

                val base64Image = encodeImageToBase64(imageFile)
                if (base64Image.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Toast.makeText(this@FormulaireActivity, "Failed to encode image.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Now call the API
                detectEmotion(base64Image) { result ->
                    // This is already running in a background thread from OkHttp
                    // We handle the UI updates on the main thread
                    runOnUiThread {
                        progressDialog.dismiss()
                        Log.d("FormulaireActivity", "Emotion detection response: $result")

                        if (result == null) {
                            Toast.makeText(this@FormulaireActivity, "Error detecting emotion. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show()
                            Log.d("FormulaireActivity", "API returned null response.")
                        } else {
                            try {
                                val jsonArray = JSONArray(result)

                                // Find the emotion with the highest score
                                var topEmotion = ""
                                var topScore = 0.0

                                for (i in 0 until jsonArray.length()) {
                                    val item = jsonArray.getJSONObject(i)
                                    val label = item.getString("label")
                                    val score = item.getDouble("score")

                                    if (score > topScore) {
                                        topScore = score
                                        topEmotion = label
                                    }
                                }

                                if (topEmotion.isNotEmpty()) {
                                    Toast.makeText(this@FormulaireActivity, "Detected emotion: $topEmotion", Toast.LENGTH_LONG).show()
                                    Log.d("FormulaireActivity", "Detected emotion: $topEmotion")

                                } else {
                                    Toast.makeText(this@FormulaireActivity, "No dominant emotion detected.", Toast.LENGTH_SHORT).show()
                                    Log.d("FormulaireActivity", "No dominant emotion found.")
                                }

                                etat_visage = topEmotion;


                                Log.d("etat_visage", "l etat est : $etat_visage")

                                ChatApiClient.generateCourseJson(
                                    titre       = courseTitleEditText.text.toString(),
                                    niveau      = proficiencyLevelSpinner.selectedItem.toString(),
                                    language    = languageSpinner.selectedItem.toString(),
                                    description = descriptionEditText.text.toString(),
                                    emotion     = etat_visage
                                ) { jsonCourse ->
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        val repo = CoursePersister(AppDatabase.getInstance(applicationContext))
                                        repo.saveCourse(jsonCourse, 1, etat_visage, languageSpinner.selectedItem.toString())

                                        val intent = Intent(this@FormulaireActivity, CoursActivity::class.java)
                                        startActivity(intent)
                                    }
                                }

                            } catch (e: Exception) {
                                Toast.makeText(this@FormulaireActivity, "Failed to parse response: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("FormulaireActivity", "JSON parsing error: ${e.message}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@FormulaireActivity, "Error processing image: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("FormulaireActivity", "Error in generateContent: ${e.message}", e)
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
                    Log.w("FormulaireActivity", "DISPLAY_NAME not found in cursor columns.")
                }
            }
        } else {
            uri.lastPathSegment?.let {
                result = File(it).name
            }
        }

        Log.d("FormulaireActivity", "Resolved file name: $result")
        return result
    }

    // Convert image file to Base64 string
    private fun encodeImageToBase64(file: File): String {
        return try {
            // Resize the image to reduce size
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true) // Most vision models expect 224x224

            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
            base64
        } catch (e: Exception) {
            Log.e("FormulaireActivity", "Error encoding image: ${e.message}", e)
            ""
        }
    }

    // Detect emotion from Base64 image string via Hugging Face API
    private fun detectEmotion(base64Image: String, onResult: (String?) -> Unit) {
        // Create a client with longer timeouts
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Format the request properly for Hugging Face - they expect a specific format
        // The image should be provided as a base64 string with proper formatting
        val jsonPayload = JSONObject()
        // For image models, the format is usually {"inputs": {"image": "BASE64_STRING"}}
        // or simply {"inputs": "BASE64_STRING"} depending on the model
        jsonPayload.put("inputs", base64Image)

        val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://api-inference.huggingface.co/models/motheecreator/vit-Facial-Expression-Recognition")
            .addHeader("Authorization", "Bearer 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")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FormulaireActivity", "API call failed: ${e.message}", e)
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                // Check if the response was successful
                if (!response.isSuccessful) {
                    Log.e("FormulaireActivity", "API error: ${response.code} - ${response.message}")
                    try {
                        // Try to get error body
                        val errorBody = response.body?.string()
                        Log.e("FormulaireActivity", "Error body: $errorBody")
                    } catch (e: Exception) {
                        Log.e("FormulaireActivity", "Failed to read error body: ${e.message}")
                    }
                    onResult(null)
                    return
                }

                val result = response.body?.string()
                Log.d("FormulaireActivity", "API response body: $result")
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
