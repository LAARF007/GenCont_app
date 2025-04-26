package com.example.gencont_app.quiz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import com.example.gencont_app.R

class QuestionAdapter(context: Context, private val questions: List<Question>) :
    ArrayAdapter<Question>(context, 0, questions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val question = questions[position]
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_question, parent, false)

        val questionText = view.findViewById<TextView>(R.id.question_text)
        val optionsGroup = view.findViewById<RadioGroup>(R.id.options_group)

        questionText.text = question.questionText

        // Vider avant de remplir
        optionsGroup.removeAllViews()

        for (option in question.options) {
            val radioButton = RadioButton(context)
            radioButton.text = option
            optionsGroup.addView(radioButton)
        }

        return view
    }
}
