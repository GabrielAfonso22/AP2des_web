package com.example.ap2des_web

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val startQuizBtn = findViewById<Button>(R.id.startQuizBtn)
        val aboutBtn = findViewById<Button>(R.id.aboutBtn)
        val shareBtn = findViewById<Button>(R.id.shareBtn)
        val openPlayerBtn = findViewById<Button>(R.id.openPlayerBtn)

        startQuizBtn.setOnClickListener {
            val name = nameInput.text.toString()
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }

        aboutBtn.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        shareBtn.setOnClickListener {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, "Venha fazer o quiz: descubra qual jogador combina com você! #QuizJogador")
            startActivity(Intent.createChooser(share, "Compartilhar"))
        }

        openPlayerBtn.setOnClickListener {
            val url = "https://en.wikipedia.org/wiki/Lionel_Messi"
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(i)
        }
    }
}