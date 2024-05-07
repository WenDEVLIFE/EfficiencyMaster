package com.example.efficiencymaster

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        // Our password layout
        val passwordLayout = findViewById<TextInputLayout>(R.id.password_layout)
        val color = ContextCompat.getColor(this, R.color.black)
        passwordLayout.endIconDrawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)


        // This is  your editText
        val usernameLayout = findViewById<EditText>(R.id.editTextText)
        val passwordEditText = findViewById<EditText>(R.id.password)

        // find the button id of our login button and set the onclick listeners
        val loginButton = findViewById<Button>(R.id.button)
        loginButton.setOnClickListener {

            // Get the username and password
            val username = usernameLayout.text.toString()
            val password = passwordEditText.text.toString()


            // Alert Dialog
            val builder =AlertDialog.Builder(this)
            builder.setTitle("Login")
            builder.setMessage("Login Successful $username $password")
            builder.setPositiveButton("OK"){dialog, which ->
                dialog.dismiss()
            }
            builder.show()

            // This will go to home fragment
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

            // Create a new user with a first and last name
            val user = hashMapOf(
                "first" to "Ada",
                "last" to "Lovelace",
                "born" to 1815
            )

// Add a new document with a generated ID
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }

        // Go to registration activity
        val registerText  = findViewById<TextView>(R.id.textbutton)
        registerText.setOnClickListener {

            // Go to registration activity
            val Intent = Intent(this, Registration::class.java)
            startActivity(Intent)
            finish()
        }

    }
}