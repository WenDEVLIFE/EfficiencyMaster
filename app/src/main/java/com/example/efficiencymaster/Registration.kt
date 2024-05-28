package com.example.efficiencymaster

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.UUID


class Registration : AppCompatActivity() {

    // This is for initializing the components

    lateinit var timertext: TextView

    private lateinit var editEmail: EditText

    private lateinit var editPassword: EditText

    private lateinit var editConfirmPassword: EditText

    private lateinit var usernameText: EditText

    private lateinit var codeText: EditText

    private lateinit var editName: EditText


    var time: Int = 0

    private var codeSent: String = "123456"

    val db = Firebase.firestore

    private lateinit var progressLoading: ProgressDialog
    private val networkManager = NetworkManager()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // This will check if user is connected to the internet.
        networkManager.checkNetworkAndExitIfNotAvailable(this)

        // This will go back to login page
        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Create an Intent to start your target activity
                val intent = Intent(this@Registration, LoginActivity::class.java)
                // Start the target activity
                startActivity(intent)
            }
        })


        // For our timer text
        timertext = findViewById(R.id.timertext)
        timertext.text = ""

        // This is for initializing the components  and  getting the id of the components
        editEmail = findViewById(R.id.emailtext)
        editPassword = findViewById(R.id.password)
        editConfirmPassword = findViewById(R.id.comfirmpassword)
        usernameText = findViewById(R.id.username)
        codeText = findViewById(R.id.codetext)
        editName = findViewById(R.id.fullname)


        // This is for sending the code
        val sendcodeButton = findViewById<Button>(R.id.sendbutton)
        sendcodeButton.setOnClickListener {
            val email = editEmail.text.toString()

            // This will check if the email contains @
        if(isValidEmail(email)) {

            // It will check the time is 0, it will send the new code
            if (time==0){
                codeSent = generateCode()
                startTimer()
                sendMail(email, codeSent)

            } else{
                Toast.makeText(this, "Please wait for 60 seconds", Toast.LENGTH_SHORT).show()

            }
        }else{

            // else it is not valid, it will error!
            editEmail.error = "Invalid email"
        }

        }

        // This is for registration
        val registerButton = findViewById<Button>(R.id.Registerbutton)
        registerButton.setOnClickListener {

            // constructors for the components info
            val username = usernameText.text.toString()
            val password = editPassword.text.toString()
            val confirmpassword = editConfirmPassword.text.toString()
            val name = editName.text.toString()
            val code = codeText.text.toString()
            val email = editEmail.text.toString()

            // This will check if the code send is equal to the sent on the email.
            if (code==codeSent){

                // This will check if password length less than 8 or greater than 13
                if (password.length<8  || password.length>=13){
                    editPassword.error = "Password must be 8-12 characters"
                    editConfirmPassword.error = "Password must be 8-12 characters"
                }else{

                    // If name is empty error
                   if(name.isEmpty()){
                          editName.error = "Name is required"
                   } else{

                       // If username is empty error
                       if(username.isEmpty()){
                           usernameText.error = "Username is required"
                       }else{


                           // If password is not equal to confirm password, it will error
                           if (password!=confirmpassword){
                               editPassword.error = "Password does not match"
                               editConfirmPassword.error = "Password does not match"
                           }else{

                               // This will check if password has Uppercase and Special Chracters
                               if (hasUpperCase(password)){
                                   if (hasSpecialChar(password)){

                                       // This will check if email is valid
                                    if(isValidEmail(email)){
                                        // Insert to the database method
                                        val userdata = arrayOf(username,password,name,email)
                                        database(userdata)
                                    }
                                   }else{
                                       editPassword.error = "Password must contain special character"
                                       editConfirmPassword.error = "Password must contain special character"
                                   }
                               }else{
                                   editPassword.error = "Password must contain uppercase"
                                   editConfirmPassword.error = "Password must contain uppercase"
                               }
                           }
                       }
                   }
                }

            } else{
                codeText.error = "Invalid code"

            }

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

    // Insert the user from the database
    private fun database(userdata: Array<String>) {

        // Declare the ProgressDialog Value
        progressLoading = ProgressDialog(this)
        progressLoading.setTitle("Adding the User ")
        progressLoading.setMessage("Please wait...")
        progressLoading.setCancelable(false)
        progressLoading.show()

        // Create a storage reference
        val storageRef = Firebase.storage.reference

        // Get the image from the drawable directory
        val drawable = resources.getDrawable(R.drawable.user_ico, this.theme)
        val bitmap = (drawable as BitmapDrawable).bitmap

        // Convert the Bitmap into a ByteArray
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        // Generate a random name for the file
        val fileName = UUID.randomUUID().toString()
        val fileReference = storageRef.child("uploads/$fileName.png")

        db.collection("User").whereEqualTo("username",userdata[0]).get().addOnSuccessListener {
            if(it.isEmpty){
                // Upload the ByteArray to Firebase Storage
                fileReference.putBytes(data).addOnSuccessListener {
                    // Get the download URL of the uploaded file
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        // User id
                        val userID = UUID.randomUUID().toString()

                        // User
                        val passwordHashed = BCrypt.withDefaults().hashToString(12, userdata[1].toCharArray())
                        val userinfo = hashMapOf(
                            "UserID" to userID,
                            "username" to userdata[0],
                            "password" to passwordHashed,
                        )

                        //  User Details
                        val userdata = hashMapOf(
                            "UserID" to userID,
                            "name" to userdata[2],
                            "email" to userdata[3],
                            "image" to fileName,
                            "imageurl" to imageUrl
                        )

                        // Add the user to the database
                        db.collection("User").add(userinfo)
                        db.collection("UserDetails").add(userdata)
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                        progressLoading.dismiss()
                        usernameText.setText("")
                        editPassword.setText("")
                        editConfirmPassword.setText("")
                        editName.setText("")
                        codeText.setText("")
                        editEmail.setText("")
                        timertext.text = ""
                        time = 0


                    }.addOnFailureListener {
                        Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                        // Hide the ProgressDialog
                        progressLoading.dismiss()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Upload failed ${e.message}", Toast.LENGTH_SHORT).show()
                    // Hide the ProgressDialog
                    progressLoading.dismiss()
                }

            }else{
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                progressLoading.dismiss()
            }
        }
    }

    // This method used to start the timer 60 seconds.
    private fun startTimer() {

        // Set the time to 60 seconds.
        time = 60

        // Start the timer
        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timertext.text = "Resend code in: " + time.toString() + "s"
                time--
            }

            override fun onFinish() {
                timertext.text = "Resend code"
            }
        }
        timer.start()
    }

    // Method used to check the email contains @ or not
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@")
    }

    // This is used to generate code 6 digits
    private fun generateCode(): String {
        val code = (100000..999999).random()
        return code.toString()
    }

    // This method is used for checking password has a special character
    private fun hasSpecialChar(password: String): Boolean {
        val regex = Regex("[^A-Za-z0-9 ]")
        return regex.containsMatchIn(password)
    }

    // This method is used for checking if password has a UpperCase
    private fun hasUpperCase(password: String): Boolean {
        val regex = Regex("[A-Z]")
        return regex.containsMatchIn(password)

    }

    // Method used for sending email and code
    private fun sendMail(email: String, code: String) {

        // Set the subject of the email
        val subject = "EfficiencyMaster Registration Verification Code"

        // Set the message of the email together with the code
        val message = "Your verification code is: $code"

        // Call the YahooMailAPI Class to send the email and execute.
        SuppressLint("Deprecation")
        YahooMailAPI(this, email, subject, message, code).execute()
    }
}