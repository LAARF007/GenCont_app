package com.example.gencont_app.register

import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gencont_app.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

   /*     val tvSignIn = findViewById<TextView>(R.id.tvSignIn)
        val text = "<b><font color='#000000'>Already A Member? </font></b><b><font color='#000000'>Sign in</font></b>"
        tvSignIn.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)*/
    }
}