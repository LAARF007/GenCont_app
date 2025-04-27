package com.example.gencont_app.formulaire

import CoursePersister
import android.app.Activity
import android.content.Intent
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
import com.example.gencont_app.configDB.database.AppDatabase
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
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

    //nextButton



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
            //courseTitleEditText proficiencyLevelSpinner languageSpinner descriptionEditText


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

        Log.d("FormulaireActivity", "Image URI: $selectedImageUri")

        val imageFile = uriToFile(selectedImageUri!!)
        Log.d("FormulaireActivity", "Converted URI to file: ${imageFile.absolutePath}")

        val base64Image = encodeImageToBase64(imageFile)

        detectEmotion(base64Image) { result ->
            runOnUiThread {
                Log.d("FormulaireActivity", "Emotion detection response: $result")

                if (result == null) {
                    Toast.makeText(this, "Error detecting emotion.", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this, "Detected emotion: $topEmotion", Toast.LENGTH_LONG).show()
                            Log.d("FormulaireActivity", "Detected emotion: $topEmotion")
                        } else {
                            Toast.makeText(this, "No dominant emotion detected.", Toast.LENGTH_SHORT).show()
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
                                repo.saveCourse(jsonCourse, 2)
                            }
                        }



                    } catch (e: Exception) {
                        Toast.makeText(this, "Failed to parse response.", Toast.LENGTH_SHORT).show()
                        Log.e("FormulaireActivity", "JSON parsing error: ${e.message}")
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
    private fun encodeImageToBase64(imageFile: File): String {
        val byteArray = imageFile.readBytes()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    // Detect emotion from Base64 image string via Hugging Face API
    private fun detectEmotion(base64Image: String, onResult: (String?) -> Unit) {
        val client = OkHttpClient()

        val jsonPayload = JSONObject()
        jsonPayload.put("inputs", base64Image)

        val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://api-inference.huggingface.co/models/motheecreator/vit-Facial-Expression-Recognition")
            .addHeader("Authorization", "Bearer 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") // Replace with your actual token
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
