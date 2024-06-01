package com.example.efficiencymaster

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragmentSetting.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragmentSetting : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var nameText:EditText
    private lateinit var profileImage: ImageView
    private lateinit var imageUri: Uri
    private lateinit var progressLoading:ProgressDialog
    var username =""
    val db = Firebase.firestore

    private val PICK_IMAGE_REQUEST = 1

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
                val profile = ProfileFragment()
                val bundle = Bundle()
                bundle.putString("username", username)
                profile.arguments = bundle
                replaceFragment(profile)
            }
        })


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_setting, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
        }

        // This will Open the drawer when the ImageButton is clicked
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.openDrawer()

        }

        // Get the EditText ids
        nameText = view.findViewById(R.id.editTextText2)
        profileImage = view.findViewById(R.id.user_icon2)
        LoadProfiles()

        // This is for upload button
        val uploadBtn = view.findViewById<Button>(R.id.button4)
        uploadBtn.setOnClickListener {
            // Open the drawer when the ImageButton is clicked
            openFileChooser()
        }

        // This is for update profile button
        val updateBtn = view.findViewById<Button>(R.id.button2)
        updateBtn.setOnClickListener {
            // Open the drawer when the ImageButton is clicked
            val name = nameText.text.toString()
            if (name.isEmpty()) {
                nameText.error = "Name is required"
                nameText.requestFocus()
                return@setOnClickListener
            }else{
                updateProfile(username, name, imageUri)
            }

        }

        // This is for back button
        val backtoProfile = view.findViewById<Button>(R.id.button5)
        backtoProfile.setOnClickListener {
            // Open the drawer when the ImageButton is clicked
            // This will go to home fragment
            val profile = ProfileFragment()
            val bundle = Bundle()
            bundle.putString("username", username)
            profile.arguments = bundle
            replaceFragment(profile)
        }
        return view;
    }
    // Method used to Replace the fragment
    private fun replaceFragment(fragment:Fragment){
        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    // This will load the profile
    private fun LoadProfiles() {
        // Load
        db.collection("User").whereEqualTo("username",username).get().addOnSuccessListener { userit ->
            if (userit.isEmpty) {
                // If the user is not found
                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
            }
            else  {
               val user =userit.documents.first()
                val userID = user.getString("UserID")

                db.collection("UserDetails").whereEqualTo("UserID", userID).get()
                    .addOnSuccessListener { userDetailit ->
                        if (userDetailit.isEmpty) {
                            // If the user is not found
                            Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                        }

                        else {

                            // Get the user details
                            val userDetail = userDetailit.documents.first()
                            val name = userDetail.getString("name")

                            val image = userDetail.data?.get("imageurl").toString()


                            // Check if the user has a name
                            nameText.setText(name)

                            // Load the image
                            Glide.with(this)
                                .load(image)
                                .into(profileImage)


                        }

                    }
            }
        }
    }
    @SuppressLint("startActivityForResult")
    private fun openFileChooser() {
        // Create an intent to open the file chooser

        val intent = Intent()

        // Set the type of file to be selected
        intent.setType("image/*")

        // Set the action to get content
        intent.setAction(Intent.ACTION_GET_CONTENT)

        // Start the activity for result
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }


    // This method is called when the file chooser is closed
    @SuppressLint("ActivityResult")  // This method is called when the file chooser is closed
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the data is not null and the data is not null
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            // Get the image uri

            imageUri = data.data!!

            // Set the image uri to the image view
            profileImage.setImageURI(imageUri)
            Toast.makeText(activity, "Image Imported", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getFileExtension(uri: Uri): String? {
        val cR = requireActivity().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    // This method is used to update the profile
    private fun updateProfile(username: String, name: String, imageUri: Uri) {

        progressLoading = ProgressDialog(context)
        progressLoading.setMessage("Updating Profile")
        progressLoading.show()
        progressLoading.setCanceledOnTouchOutside(false)

        // Create a storage reference
        val storageRef: StorageReference = FirebaseStorage.getInstance().getReference("uploads")

        // Check if the image uri is not null
        // Get the file extension
        val fileExtension = getFileExtension(imageUri)

        // Create a file reference
        val fileReference = storageRef.child(System.currentTimeMillis().toString() + "." + fileExtension)

        // Upload the file to the storage
        fileReference.putFile(imageUri).addOnSuccessListener {
            // Get the download url
            fileReference.downloadUrl.addOnSuccessListener {
                val newImageUrl = it.toString()
                // Update the profile
                db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener { userit ->
                    if (userit.isEmpty) {
                        // If the user is not found
                        Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                        progressLoading.dismiss()
                    } else {
                        val user = userit.documents.first()
                        val userID = user.getString("UserID")

                        db.collection("UserDetails").whereEqualTo("UserID", userID).get()
                            .addOnSuccessListener { userDetailit ->
                                if (userDetailit.isEmpty) {
                                    // If the user is not found
                                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                                    progressLoading.dismiss()
                                } else {
                                    // Get the user details
                                    val userDetail = userDetailit.documents.first()
                                    val oldImageName = userDetail.getString("image")
                                    val userDetailID = userDetail.id

                                    // Delete the old image from Firebase Storage
                                    if (oldImageName != null) {
                                        val oldImageRef = FirebaseStorage.getInstance().getReference("uploads/$oldImageName")
                                        oldImageRef.delete()
                                        progressLoading.dismiss()
                                    }

                                    // Update the user details
                                    db.collection("UserDetails").document(userDetailID).update("name", name, "imageurl", newImageUrl , "image", fileReference.name)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                                            progressLoading.dismiss()

                                            (activity as MainActivity).loadUserStats()

                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Profile Update Failed", Toast.LENGTH_SHORT).show()
                                            progressLoading.dismiss()
                                        }
                                }
                            }
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
         * @return A new instance of fragment UserFragmentSetting.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragmentSetting().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}