package com.example.efficiencymaster

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    val db = Firebase.firestore
    lateinit var  ProgressLoading: ProgressDialog
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

            if(username.isEmpty()){
                usernameLayout.error = "Please enter your username"
            }
            else{
                if(password.isEmpty()){
                    passwordEditText.error = "Please enter your password"
                }else{
                    LoginVerification(username, password)
                }
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

    fun LoginVerification(username: String, password: String) {
        ProgressLoading = ProgressDialog(this)
        ProgressLoading.setTitle("Loading")
        ProgressLoading.setMessage("Please wait...")
        ProgressLoading.setCanceledOnTouchOutside(false)
        ProgressLoading.show()

        db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener {
            if (it.isEmpty) {
                Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                 ProgressLoading.dismiss()
            }else{
                for (document in it) {
                    val PASSWORD = document.getString("password")
                    val result = BCrypt.verifyer().verify(password.toCharArray(), PASSWORD)

                    if(result.verified){
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        ProgressLoading.dismiss()
                        val Intent = Intent(this, MainActivity::class.java)
                        Intent.putExtra("username", username)
                        startActivity(Intent)
                        finish()
                    }else{
                        Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show()
                        ProgressLoading.dismiss()

                    }

                }
            }
        }

    }
}