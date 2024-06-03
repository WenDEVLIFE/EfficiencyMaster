package com.example.efficiencymaster

import android.app.ProgressDialog
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import at.favre.lib.crypto.bcrypt.BCrypt
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PasswordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var username =""
    private lateinit var profilePicture: ImageView
    private lateinit var passwordField: EditText  // Password field
    private lateinit var passwordField2: EditText  // Password field
    private lateinit var progressLoading:ProgressDialog
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
        val view = inflater.inflate(R.layout.fragment_password2, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username= it.getString("username").toString()
        }

        // Find the ImageButton in the fragment_group.xml layout
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.openDrawer()

        }


        // Profile picture
        profilePicture = view.findViewById(R.id.user_icon2)
        loadUserStats()

        // Password field
        passwordField = view.findViewById(R.id.password)
        passwordField2 = view.findViewById(R.id.confirmpassword)

        // Our password layout
        // password layout 1
        val passwordLayout = view.findViewById<TextInputLayout>(R.id.password_layout)
        val color = ContextCompat.getColor(requireContext(), R.color.black)
        passwordLayout.endIconDrawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)

        // password layout 2
        val passwordLayout1 = view.findViewById<TextInputLayout>(R.id.password_layout1)
        passwordLayout1.endIconDrawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)

        // Update button
        val updateBtn = view.findViewById<Button>(R.id.button2)
        updateBtn.setOnClickListener {
            // Get the password
            val password = passwordField.text.toString()
            val password2 = passwordField2.text.toString()

            // Check if the password is empty
            if (password.isEmpty() || password2.isEmpty()) {
                passwordField.error = "Password is required"
                passwordField2.error = "Password is required"
                return@setOnClickListener
            }else{

                // for checking special character
                val specialChar = hasSpecialChar(password)

                // for checking uppercase
                val upperCase = hasUpperCase(password)

                // Check if the password has a special character
                if (!specialChar) {
                    passwordField.error = "Password must contain a special character"
                    return@setOnClickListener

                    // Check if password has uppercase
                }else if (!upperCase) {
                    passwordField.error = "Password must contain an uppercase letter"
                    return@setOnClickListener
                }

                // Check if the password is between 8 and 13 characters
                else if (password2.length <=8 ||password2.length >13){
                    passwordField.error = "Password must be between 8 and 13 characters"
                    return@setOnClickListener
                } else{
                    // Progress dialog
                    progressLoading = ProgressDialog(requireContext())
                    progressLoading.setTitle("Loading")
                    progressLoading.setMessage("Updating password")
                    progressLoading.show()
                    progressLoading.setCanceledOnTouchOutside(false)

                    // Check if the password is correct
                    db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener { userDocuments ->
                        if (userDocuments.isEmpty) {
                            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                            progressLoading.dismiss()
                        } else {

                            // Get the user password
                            val userDocument = userDocuments.documents.first()
                            val userPassword = userDocument.getString("password")

                            // use bycrypt to encrypt the password
                            val result = BCrypt.verifyer().verify(password.toCharArray(), userPassword)


                            // If the result is verified, it will proceed to the main activity
                            if(result.verified){

                                // hash the new password using bycrypt and update the password
                                val hashed = BCrypt.withDefaults().hashToString(12, password2.toCharArray())

                                // Update the password
                                db.collection("User").document(userDocument.id).update("password", hashed).addOnSuccessListener {
                                    success()
                                    progressLoading.dismiss()
                                    passwordField.setText("")
                                    passwordField2.setText("")
                                }.addOnFailureListener{
                                    Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show()
                                    progressLoading.dismiss()
                                }




                                // Else it will show incorrect password
                            }else{
                                passwordField.error = "Incorrect Password"
                                warning()
                                progressLoading.dismiss()
                            }
                        }
                    }
                }

            }

        }

        // back button
        val backBtn = view.findViewById<Button>(R.id.button4)
        backBtn.setOnClickListener {
            // Replace with your previous fragment here
            // This will go to home fragment
            val profile = HomeFragmentation()
            val bundle = Bundle()
            bundle.putString("username", username)
            profile.arguments = bundle
            replaceFragment(profile)
        }



        return view
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

    // Method used to Replace the fragment
    private fun replaceFragment(fragment:Fragment){
        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    // This method is used to pop up warning dialog
    private  fun warning(){

        // below are the customize alert dialgo components and etc.
        val builder1 = android.app.AlertDialog.Builder(context)
        val inflater1 = layoutInflater
        val dialogLayout1 = inflater1.inflate(R.layout.message_layout, null)
        val titleText1= dialogLayout1.findViewById<TextView>(R.id.dialog_title)
        val messageText1 = dialogLayout1.findViewById<TextView>(R.id.dialog_message)
        val button1 = dialogLayout1.findViewById<Button>(R.id.dialog_button)
        button1.text = buildString {
            append("Ok")
        }
        val imageView2 = dialogLayout1.findViewById<ImageView>(R.id.imageView2)

        Glide.with(requireContext())
            .asGif()
            .load(R.drawable.alert)
            .into(imageView2)
        imageView2.scaleType = ImageView.ScaleType.FIT_CENTER
        val params1 = imageView2.layoutParams
        val scale1 = resources.displayMetrics.density
        params1.width = (100 * scale1).toInt()
        params1.height = (100 * scale1).toInt()
        imageView2.layoutParams = params1
        titleText1.text = buildString {
            append("Password Alert")
        }
        messageText1.text = buildString {
            append("Password is incorrect, please try again")
        }

        val dialog1 = builder1.setView(dialogLayout1).create()

        dialog1.show()
        button1.setOnClickListener{
            dialog1.dismiss()
        }
    }

    // This method is used to pop up success dialog
    private fun success(){

        // below are the customize alert dialgo components and etc.
        val builder1 = android.app.AlertDialog.Builder(context)
        val inflater1 = layoutInflater
        val dialogLayout1 = inflater1.inflate(R.layout.message_layout, null)
        val titleText1= dialogLayout1.findViewById<TextView>(R.id.dialog_title)
        val messageText1 = dialogLayout1.findViewById<TextView>(R.id.dialog_message)
        val button1 = dialogLayout1.findViewById<Button>(R.id.dialog_button)
        button1.text = buildString {
            append("Ok")
        }
        val imageView2 = dialogLayout1.findViewById<ImageView>(R.id.imageView2)

        Glide.with(requireContext())
            .asGif()
            .load(R.drawable.double_check)
            .into(imageView2)
        imageView2.scaleType = ImageView.ScaleType.FIT_CENTER
        val params1 = imageView2.layoutParams
        val scale1 = resources.displayMetrics.density
        params1.width = (100 * scale1).toInt()
        params1.height = (100 * scale1).toInt()
        imageView2.layoutParams = params1
        titleText1.text = buildString {
            append("Password Alert")
        }
        messageText1.text = buildString {
            append("Password updated successfully")
        }

        val dialog1 = builder1.setView(dialogLayout1).create()

        dialog1.show()
        button1.setOnClickListener{
            dialog1.dismiss()
        }
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}