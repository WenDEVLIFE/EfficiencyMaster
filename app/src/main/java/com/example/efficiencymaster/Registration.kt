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

    // This is for initializing the components

    lateinit var timertext: TextView

    lateinit var EditEmail: EditText

    lateinit var EditPassword: EditText

    lateinit var EditConfirmPassword: EditText

    lateinit var UsernameText: EditText

    lateinit var CodeText: EditText

    lateinit var EditName: EditText

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

        // This is for initializing the components  and  getting the id of the components
        EditEmail = findViewById<EditText>(R.id.emailtext)
        EditPassword = findViewById<EditText>(R.id.password)
        EditConfirmPassword = findViewById<EditText>(R.id.comfirmpassword)
        UsernameText = findViewById<EditText>(R.id.username)
        CodeText = findViewById<EditText>(R.id.codetext)
        EditName = findViewById<EditText>(R.id.fullname)


        // This is for sending the code
        val sendcodeButton = findViewById<Button>(R.id.sendbutton)
        sendcodeButton.setOnClickListener {

        }

        // This is for registration
        val registerButton = findViewById<Button>(R.id.Registerbutton)
        registerButton.setOnClickListener {

        }

        //  This is for going back to  login page
        val backButton = findViewById<Button>(R.id.Backbutton)
        backButton.setOnClickListener {

            //  Go to login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
}