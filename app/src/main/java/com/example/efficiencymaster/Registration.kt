package com.example.efficiencymaster

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

    var time: Int = 0

    var code_sent: String = "123456"

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
            val email = EditEmail.text.toString()

            if (time==0){
                code_sent = generateCode()
                startTimer()

            } else{
                Toast.makeText(this, "Please wait for 60 seconds", Toast.LENGTH_SHORT).show()

            }

        }

        // This is for registration
        val registerButton = findViewById<Button>(R.id.Registerbutton)
        registerButton.setOnClickListener {
            val username = UsernameText.text.toString()
            val password = EditPassword.text.toString()
            val confirmpassword = EditConfirmPassword.text.toString()
            val name = EditName.text.toString()
            val code = CodeText.text.toString()

            if (code==code_sent){
                if (password.length<8  || password.length>=13){
                    EditPassword.error = "Password must be 8-12 characters"
                    EditConfirmPassword.error = "Password must be 8-12 characters"
                }else{
                   if(name.isEmpty()){
                          EditName.error = "Name is required"
                   } else{
                       if(username.isEmpty()){
                           UsernameText.error = "Username is required"
                       }else{
                           if (password!=confirmpassword){
                               EditPassword.error = "Password does not match"
                               EditConfirmPassword.error = "Password does not match"
                           }else{
                               if (HasUpperCase(password)){
                                   if (hasSpecialChar(password)){
                                       //  Go to login activity
                                       val intent = Intent(this, LoginActivity::class.java)
                                       startActivity(intent)
                                       finish()
                                   }else{
                                       EditPassword.error = "Password must contain special character"
                                       EditConfirmPassword.error = "Password must contain special character"
                                   }
                               }else{
                                   EditPassword.error = "Password must contain uppercase"
                                   EditConfirmPassword.error = "Password must contain uppercase"
                               }
                           }
                       }
                   }
                }

            } else{

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

    fun startTimer() {
        time = 60
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

    fun generateCode(): String {
        val code = (100000..999999).random()
        return code.toString()
    }

    fun hasSpecialChar(password: String): Boolean {
        val regex = Regex("[^A-Za-z0-9 ]")
        return regex.containsMatchIn(password)
    }

    fun HasUpperCase(password: String): Boolean {
        val regex = Regex("[A-Z]")
        return regex.containsMatchIn(password)

    }
}