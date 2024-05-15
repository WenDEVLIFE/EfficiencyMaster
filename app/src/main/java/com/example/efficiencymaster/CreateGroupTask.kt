package com.example.efficiencymaster

import android.animation.AnimatorInflater
import android.app.AlertDialog
import android.app.ProgressDialog
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateGroupTask.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateGroupTask : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var username =""
    val db = Firebase.firestore
    lateinit var ProgressLoading: ProgressDialog
    lateinit var groupName:EditText
    lateinit var groupDescription:EditText


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
        // Inflate the layout for this fragment
        val view  = inflater.inflate(R.layout.fragment_create_group, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
        }

        // Get the ImageButton from the view
        val ImageButton = view.findViewById<ImageButton>(R.id.imageButton)
        ImageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.OpenDrawer()

        }

        // Get the EditText from the view
        groupName = view.findViewById(R.id.editTextText2)
        groupDescription = view.findViewById(R.id.desscripts)

        // Get the Button from the view
        val createGroupBtn = view.findViewById<Button>(R.id.button2)
        createGroupBtn.setOnClickListener {
            val groupName1 = groupName.text.toString()
            val groupDescription1 = groupDescription.text.toString()

            if(groupName1.isEmpty()) {
                groupName.error = "Please Enter Task Name"
            }else{
                if(groupDescription1.isEmpty()) {
                    groupDescription.error = "Please Enter Task Description"
                }else{

                    InsertGroup(groupName1, groupDescription1)
                    ProgressLoading = ProgressDialog(context)
                    ProgressLoading.setTitle("Creating Group")
                    ProgressLoading.setMessage("Please wait while we create the group")
                    ProgressLoading.setCanceledOnTouchOutside(false)
                    ProgressLoading.show()

                }
            }
        }

    return view
    }

    fun InsertGroup(groupName1: String , groupDescription1: String) {

        db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener {
            if (it.isEmpty) {
                // If the user does not exist
            }else{
                for (document in it){
                    val ID = document.data["UserID"].toString()

                    val IDGroup = Random.nextInt(123568, 9999999)
                    val group = hashMapOf(
                        "GroupID" to IDGroup,
                        "GroupName" to groupName1,
                        "GroupDescription" to groupDescription1,
                        "UserID" to ID
                    )

                   db.collection("Group").whereEqualTo("GroupName", groupName1).whereEqualTo("UserID",ID).get().addOnSuccessListener {
                       if (it.isEmpty) {
                           db.collection("Group").add(group)

                           db.collection("ProgresssUser").whereEqualTo("UserID",ID).get().addOnSuccessListener {
                               // Check if the user has any progress
                               val XpData = Random.nextInt(100, 1000)
                               if(it.isEmpty){

                                   // Call the create xp method to create new progress for the user.
                                   CreateXp(ID, XpData)
                               }

                               // If the user has progress
                               else{

                                   // Update the user progress
                                   for (document in it){

                                       // Create a dialog to show the user the progress below
                                       val builder = AlertDialog.Builder(context)
                                       val inflater = layoutInflater
                                       val dialogLayout = inflater.inflate(R.layout.message_layout, null)

                                       val titleText = dialogLayout.findViewById<TextView>(R.id.dialog_title)
                                       val messageText = dialogLayout.findViewById<TextView>(R.id.dialog_message)
                                       val button = dialogLayout.findViewById<Button>(R.id.dialog_button)

                                       // added a animation when it pop up
                                       val floatingAnimation = AnimatorInflater.loadAnimator(context, R.animator.floatingxml)
                                       val imageView = dialogLayout.findViewById<ImageView>(R.id.imageView2)
                                       floatingAnimation.setTarget(imageView)
                                       floatingAnimation.start()

                                       titleText.text = "Done Creating Group"
                                       messageText.text = "You have gained $XpData xp for creating a group"

                                       val dialog = builder.setView(dialogLayout).create() // Create AlertDialog instance

                                       button.setOnClickListener {
                                           // Handle button click here
                                           dialog.dismiss()
                                       }

                                       dialog.show() // Show the dialog

                                       // Update the user progress in the database and add it to the existing progress
                                       val xp = document.get("ProgressXp").toString().toInt()
                                       val UpdatedXP:Int = xp + XpData

                                       // Update the user progress
                                       db.collection("ProgresssUser").document(it.documents[0].id).update("ProgressXp", UpdatedXP)

                                       // clear the text fields
                                       groupName.text.clear()
                                       groupDescription.text.clear()
                                       ProgressLoading.dismiss()
                                       Toast.makeText(context, "Task Inserted", Toast.LENGTH_SHORT).show()
                                   }
                               }
                           }
                       }else{
                           // If the group already exists
                            Toast.makeText(context, "Group Already Exists", Toast.LENGTH_SHORT).show()
                            ProgressLoading.dismiss()
                       }
                   }
                }
            }
        }

    }

    // This Method is used to create xp for the user
    fun CreateXp (ID: String, XpData: Int) {

        // Hashmap for progress
        val progressXp = hashMapOf(
            "UserID" to ID,
            "ProgressXp" to XpData,

            )

        // Add the progress to the database
        db.collection("ProgresssUser").add(progressXp).addOnSuccessListener {

            // Customize dialog below here
            val builder = AlertDialog.Builder(context)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.message_layout, null)

            val titleText = dialogLayout.findViewById<TextView>(R.id.dialog_title)
            val messageText = dialogLayout.findViewById<TextView>(R.id.dialog_message)
            val button = dialogLayout.findViewById<Button>(R.id.dialog_button)

            titleText.text = "Done Creating Group"
            messageText.text = "You have gained $XpData xp for creating a group"

            val dialog = builder.setView(dialogLayout).create() // Create AlertDialog instance
            button.setOnClickListener {
                // Handle button click here

                dialog.dismiss()

            }

            builder.setView(dialogLayout)
            builder.show()

            Toast.makeText(context, "Group Inserted", Toast.LENGTH_SHORT).show()
            groupName.text.clear()
            groupDescription.text.clear()
            ProgressLoading.dismiss()
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateGroupTask.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateGroupTask().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}