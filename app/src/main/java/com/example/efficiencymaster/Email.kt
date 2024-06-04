package com.example.efficiencymaster

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Email.newInstance] factory method to
 * create an instance of this fragment.
 */
class Email : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var oldEmailText :TextInputEditText
    private lateinit var newEmailText :TextInputEditText
    private lateinit var CodeText :TextInputEditText
    private val networkManager = NetworkManager()
    private lateinit var profileImage : ImageView // This is used to display the profile image
    lateinit var timertext: TextView


    var time: Int = 0

    var username  = ""
    var codeSend =""
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Handle back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Replace with your previous fragment here
                // This will go to home fragment
                val profile = HomeFragmentation()
                val bundle = Bundle()
                bundle.putString("username", username)
                profile.arguments = bundle
                replaceFragment(profile)
            }
        })

        // This will check if user is connected to the internet.
        networkManager.checkNetworkAndExitIfNotAvailable(requireContext())

        // Inflate the layout for this fragment
        val view  =   inflater.inflate(R.layout.fragment_email, container, false)

        // Get the ImageButton from the view
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.openDrawer()

        }

        // Get the TextInputEditText from the view
        oldEmailText = view.findViewById(R.id.emailtext)
        newEmailText = view.findViewById(R.id.emailtext1)
        CodeText = view.findViewById(R.id.codetext)
        timertext = view.findViewById(R.id.titlestext2)
        loadUserStats() // load the profile

        val updateBtn = view.findViewById<Button>(R.id.button2)
        updateBtn.setOnClickListener {
            val oldEmail = oldEmailText.text.toString()
            val newEmail = newEmailText.text.toString()
            val code = CodeText.text.toString()


        }

        val backtbn = view.findViewById<Button>(R.id.button4)
        backtbn.setOnClickListener {
            val profile = HomeFragmentation()
            val bundle = Bundle()
            bundle.putString("username", username)
            profile.arguments = bundle
            replaceFragment(profile)
        }

        val sendCodeBtn = view.findViewById<Button>(R.id.button7)
        sendCodeBtn.setOnClickListener {
            val email = oldEmailText.text.toString()
            if (!isValidEmail(email)) {
                oldEmailText.error = "Invalid Email"
                return@setOnClickListener
            }
            val code = generateCode()
            codeSend = code
            sendMail(email, code)
            startTimer()

        }



        return view
    }

    // Method used to Replace the fragment
    private fun replaceFragment(fragment:Fragment){
        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

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

    // Method used for sending email and code
    private fun sendMail(email: String, code: String) {

        // Set the subject of the email
        val subject = "EfficiencyMaster Registration Verification Code"

        // Set the message of the email together with the code
        val message = "Your verification code is: $code"

        // Call the YahooMailAPI Class to send the email and execute.
        SuppressLint("Deprecation")
        YahooMailAPI(requireContext(), email, subject, message, code).execute()
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
    // This method used for loading the user stats.
    fun loadUserStats(){

        // Check if the user exists before load it
        db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener { userDocuments ->
            if (userDocuments.isEmpty) {
                Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
            } else {
                val userDocument = userDocuments.documents.first()
                val userID = userDocument.getString("UserID")

                // This will load the user details and check if the user has any progress
                db.collection("UserDetails").whereEqualTo("UserID", userID).get().addOnSuccessListener { userDetailsDocuments ->

                    if (userDetailsDocuments.isEmpty) {
                        Toast.makeText(requireContext(), "User details not found", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val userDetailsDocument = userDetailsDocuments.documents.first()
                        val image = userDetailsDocument.getString("imageurl")


                        // Set the retrieve Imageurl to image
                        Glide.with(this).load(image).into(profileImage)
                    }
                }
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Email.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Email().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}