package com.example.labtest03

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class onboarding1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding1)

        val startButton = findViewById<Button>(R.id.startButton)

        startButton.setOnClickListener {
            // Move to your MainActivity when button clicked
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close onboarding so user won’t come back with back button
        }
    }
}