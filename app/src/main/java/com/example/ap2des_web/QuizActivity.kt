package com.example.ap2des_web

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Start the first question activity
        val name = intent.getStringExtra("name") ?: ""
        val startIntent = Intent(this, QuestionActivity::class.java)
        startIntent.putExtra("name", name)
        startIntent.putExtra("qIndex", 0)
        startIntent.putExtra("score", 0)
        startActivity(startIntent)
        finish()
    }
}
