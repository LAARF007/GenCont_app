package com.example.gencont_app.formulaire

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gencont_app.R
import com.google.android.material.textfield.TextInputLayout

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formulaire)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }





            // Initialize UI elements
            initializeUI()

            // Set up button click listeners
            setupClickListeners()
        }

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
                //  Handle image generation logic here
                if (selectedImageUri != null) {
                    generateContent()
                } else {
                    Toast.makeText(this, "Please select an image first.", Toast.LENGTH_SHORT).show()
                }
            }
        }

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

        private fun pickImageFromGallery() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
                selectedImageUri = data?.data
                imagePreview.setImageURI(selectedImageUri)
                imagePreview.visibility = View.VISIBLE
            }
        }

        private fun generateContent() {

            Toast.makeText(this, "Generating content...", Toast.LENGTH_SHORT).show()
        }


}