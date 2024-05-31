package com.example.efficiencymaster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var usernameText: TextView
    private lateinit var nameText:TextView
    private lateinit var userIDText:TextView
    private lateinit var newsFeed:RecyclerView
    private lateinit var achievementViewer: RecyclerView
    private lateinit var profileView: ImageView
    private val networkManager = NetworkManager()

    val db = Firebase.firestore

    var username =""

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
                val homeFragmentation = HomeFragmentation()
                val bundle = Bundle()
                bundle.putString("username", username)
                homeFragmentation.arguments = bundle
                replaceFragment(homeFragmentation)
            }
        })

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
        }

        // This will check if user is connected to the internet.
        networkManager.checkNetworkAndExitIfNotAvailable(requireContext())

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)

        // This will Open the drawer when the ImageButton is clicked
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.openDrawer()

        }

        // get the textviews id
        usernameText = view.findViewById(R.id.textview)
        nameText = view.findViewById(R.id.textview2)
        userIDText = view.findViewById(R.id.textview3)
        profileView = view.findViewById(R.id.user_icon)
        loadProfile()

        val editBtn = view.findViewById<ImageButton>(R.id.imageButton4)
        editBtn.setOnClickListener {
            Toast.makeText(context, "Edit Profile", Toast.LENGTH_SHORT).show()
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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // This will load the profile of the user from the database
    private fun loadProfile() {
        // Load
        db.collection("User").whereEqualTo("username",username).get().addOnSuccessListener { userit ->

            for ( user in userit) {

                val username = user.getString("username")
                val userID = user.getString("UserID")

              db.collection("UserDetails").whereEqualTo("UserID",userID).get().addOnSuccessListener { userDetailit ->

                  for ( userDetail in userDetailit) {

                      val name = userDetail.getString("name")
                      val image = userDetail.data["imageurl"].toString()

                      // display the details
                      usernameText.text = "Username: $username"
                      nameText.text =  "Name: $name"
                      userIDText.text = "UserID: $userID"


                        // Load the image
                        Glide.with(this)
                            .load(image)
                            .into(profileView)


                  }

              }
            }
        }
    }
}