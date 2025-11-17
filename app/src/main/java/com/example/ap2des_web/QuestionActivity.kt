package com.example.ap2des_web

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class QuestionActivity : AppCompatActivity(), QuestionFragment.Callback {

    private val totalQuestions = 5
    private var qIndex = 0
    private var score = 0
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.ap2des_web.R.layout.activity_question)

        qIndex = intent.getIntExtra("qIndex", 0)
        score = intent.getIntExtra("score", 0)
        name = intent.getStringExtra("name") ?: ""

        showQuestion(qIndex)
    }

    private fun showQuestion(index: Int) {
        val frag = QuestionFragment.create(index)
        supportFragmentManager.beginTransaction()
            .replace(com.example.ap2des_web.R.id.fragment_container, frag)
            .commit()
    }

    override fun onAnswerSelected(scoreDelta: Int) {
        score += scoreDelta
        qIndex += 1
        if (qIndex < totalQuestions) {
            showQuestion(qIndex)
        } else {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("score", score)
            intent.putExtra("name", name)
            startActivity(intent)
            finish()
        }
    }
}
