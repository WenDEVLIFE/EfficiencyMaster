package com.example.efficiencymaster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Registration : AppCompatActivity() {

    lateinit var timertext: TextView

    lateinit var EditEmail: EditText

    lateinit var EditPassword: EditText

    lateinit var EditConfirmPassword: EditText

    lateinit var UsernameText: EditText

    lateinit var CodeText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        timertext = findViewById<TextView>(R.id.timertext)

        // This is for sending the code
        val sendcodeButton = findViewById<Button>(R.id.sendbutton)
        sendcodeButton.setOnClickListener {

        }
        val registerButton = findViewById<Button>(R.id.Registerbutton)
        registerButton.setOnClickListener {

        }
        val backButton = findViewById<Button>(R.id.Backbutton)
        backButton.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
}