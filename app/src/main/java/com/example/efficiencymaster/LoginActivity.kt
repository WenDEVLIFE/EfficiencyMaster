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
    private lateinit var progressLoading: ProgressDialog
    private val networkManager = NetworkManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        load()

        // This will check if user is connected to the internet.
        networkManager.checkNetworkAndExitIfNotAvailable(this)

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

            // This will check if the username is empty, error message.
            if(username.isEmpty()){
                usernameLayout.error = "Please enter your username"
            }
            else{

                // This will check if the password is empty, error message.
                if(password.isEmpty()){
                    passwordEditText.error = "Please enter your password"
                }else{

                    // Else all the fields are not empty it will send the value
                    // to the loginVerification method
                    loginVerification(username, password)
                }
            }

        }

        // Go to registration activity
        val registerText  = findViewById<TextView>(R.id.textbutton)
        registerText.setOnClickListener {

            // Go to registration activity
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
            finish()
        }

    }


    // This method used for login verifications
    private fun loginVerification(username: String, password: String) {

        // Declare the ProgressLoading value
        progressLoading = ProgressDialog(this)
        progressLoading.setTitle("Loading")
        progressLoading.setMessage("Please wait...")
        progressLoading.setCanceledOnTouchOutside(false)
        progressLoading.show()

        // Check if the user exists before inserting
        db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener {
          // If the user does not exist it will error
            if (it.isEmpty) {
                Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                 progressLoading.dismiss()
            }else{

                // This will load the documents of user
                for (document in it) {

                    // Get the retrieve password and use bycrypt to verify the passwoord
                    val retrievepass = document.getString("password")
                    val result = BCrypt.verifyer().verify(password.toCharArray(), retrievepass)


                    // If the result is verified, it will proceed to the main activity
                    if(result.verified){
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        progressLoading.dismiss()


                        // Create an instance of SessionManager and log in the user
                        val sessionManager = SessionManager(this)
                        sessionManager.userLogin(username)


                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("username", username)
                        startActivity(intent)
                        finish()

                        // Else it will show incorrect password
                    }else{
                        Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show()
                        progressLoading.dismiss()

                    }

                }
            }
        }

    }

    // This method  will check if the user session exiist
    private fun load() {
        // Create an instance of SessionManager
        val sessionManager = SessionManager(this)

        // Check if the user is logged in
        if (sessionManager.isUserLoggedIn()) {
            // If the user is logged in, redirect them to the main activity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("username", sessionManager.getUser)
            startActivity(intent)
            finish()
        }
        // If the user is not logged in, do nothing and let them stay on the LoginActivity
    }
}