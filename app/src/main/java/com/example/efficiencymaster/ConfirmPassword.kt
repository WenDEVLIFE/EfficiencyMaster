package com.example.efficiencymaster

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import at.favre.lib.crypto.bcrypt.BCrypt
import com.bumptech.glide.Glide
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ConfirmPassword.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConfirmPassword : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var profilePicture: ImageView
    private lateinit var passwordField: EditText  // Password field
    var username =""
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username= it.getString("username").toString()
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

        // Inflate the layout for this fragment
        val view  = inflater.inflate(R.layout.fragment_password, container, false)

        profilePicture = view.findViewById(R.id.user_icon2)
        passwordField = view.findViewById(R.id.password)
            loadUserStats()

        val submitBtn = view.findViewById<Button>(R.id.button)
        submitBtn.setOnClickListener{
            val password = passwordField.text.toString()

            // Check if the password is correct
            db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener { userDocuments ->
                if (userDocuments.isEmpty) {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                } else {
                    val userDocument = userDocuments.documents.first()
                    val userPassword = userDocument.getString("password")

                    val result = BCrypt.verifyer().verify(password.toCharArray(), userPassword)


                    // If the result is verified, it will proceed to the main activity
                    if(result.verified){
                        Toast.makeText(requireContext(), "Password Successful", Toast.LENGTH_SHORT).show()




                        // Else it will show incorrect password
                    }else{
                        Toast.makeText(requireContext(), "Incorrect Password", Toast.LENGTH_SHORT).show()

                    }
                }
            }
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
                        Glide.with(this).load(image).into(profilePicture)
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
         * @return A new instance of fragment ConfirmPassword.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConfirmPassword().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}