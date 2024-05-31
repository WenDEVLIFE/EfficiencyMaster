package com.example.efficiencymaster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback

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
    private lateinit var emailText:EditText

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
        emailText = view.findViewById(R.id.email)

        val uploadBtn = view.findViewById<ImageButton>(R.id.button4)
        uploadBtn.setOnClickListener {
            // Open the drawer when the ImageButton is clicked

        }

        val updateBtn = view.findViewById<ImageButton>(R.id.button2)
        updateBtn.setOnClickListener {
            // Open the drawer when the ImageButton is clicked

        }

        val backtoProfile = view.findViewById<ImageButton>(R.id.button5)
        backtoProfile.setOnClickListener {
            // Open the drawer when the ImageButton is clicked

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