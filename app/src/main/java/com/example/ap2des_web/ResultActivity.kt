package com.example.ap2des_web

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.content.ContextCompat
import android.widget.ScrollView

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val score = intent.getIntExtra("score", 0)
        val name = intent.getStringExtra("name") ?: ""

        val resultRoot = findViewById<ScrollView>(R.id.resultRoot)
        val playerImage = findViewById<ImageView>(R.id.playerImage)
        val resultText = findViewById<TextView>(R.id.resultText)
        val shareBtn = findViewById<Button>(R.id.shareResultBtn)
        val openWebBtn = findViewById<Button>(R.id.openWebBtn)

        val player = when {
            score >= 8 -> "Cristiano Ronaldo"
            score >= 5 -> "Lionel Messi"
            else -> "Kylian Mbappé"
        }

        val drawableId = when (player) {
            "Cristiano Ronaldo" -> R.drawable.player_cr
            "Lionel Messi" -> R.drawable.player_lm
            else -> R.drawable.player_km
        }
        playerImage.setImageResource(drawableId)

        // set background based on player (use _new resources to avoid corrupt originals)
        val bgId = when (player) {
            "Cristiano Ronaldo" -> R.drawable.bg_player_cr_new
            "Lionel Messi" -> R.drawable.bg_player_lm_new
            else -> R.drawable.bg_player_km_new
        }
        resultRoot.setBackgroundResource(bgId)

        // ensure text contrast
        resultText.setTextColor(ContextCompat.getColor(this, android.R.color.black))

        resultText.text = getString(R.string.result_text_format, name, player, score)

        shareBtn.setOnClickListener {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text_format, name, player))
            startActivity(Intent.createChooser(share, getString(R.string.share_prompt)))
        }

        openWebBtn.setOnClickListener {
            // implicit intent to open a player page
            val url = when (player) {
                "Cristiano Ronaldo" -> "https://en.wikipedia.org/wiki/Cristiano_Ronaldo"
                "Lionel Messi" -> "https://en.wikipedia.org/wiki/Lionel_Messi"
                else -> "https://en.wikipedia.org/wiki/Kylian_Mbapp%C3%A9"
            }
            val i = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(i)
        }
    }
}
