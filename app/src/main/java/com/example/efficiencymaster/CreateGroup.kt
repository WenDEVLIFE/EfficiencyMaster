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
import java.time.LocalDate
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateGroup.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateGroup : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var username =""
    val db = Firebase.firestore
    private lateinit var progressLoading: ProgressDialog
    private lateinit var groupName:EditText
    private lateinit var groupDescription:EditText
    private val networkManager = NetworkManager()


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

        // This will check if user is connected to the internet.
        networkManager.checkNetworkAndExitIfNotAvailable(requireContext())


        // Inflate the layout for this fragment
        val view  = inflater.inflate(R.layout.fragment_create_group, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
        }

        // Get the ImageButton from the view
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.openDrawer()

        }

        // Get the EditText from the view
        groupName = view.findViewById(R.id.editTextText2)
        groupDescription = view.findViewById(R.id.desscripts)

        // Get the Button from the view
        val createGroupBtn = view.findViewById<Button>(R.id.button2)
        createGroupBtn.setOnClickListener {
            val groupName1 = groupName.text.toString()
            val groupDescription1 = groupDescription.text.toString()

            // Check if the group name is empty
            if(groupName1.isEmpty()) {
                groupName.error = "Please Enter Task Name"
            }else{

                // Check if description is  empty
                if(groupDescription1.isEmpty()) {
                    groupDescription.error = "Please Enter Task Description"
                }else{

                    // Call the insertGroup method to insert the group.
                    insertGroup(groupName1, groupDescription1)
                    progressLoading = ProgressDialog(context)
                    progressLoading.setTitle("Creating Group")
                    progressLoading.setMessage("Please wait while we create the group")
                    progressLoading.setCanceledOnTouchOutside(false)
                    progressLoading.show()

                }
            }
        }

        //  button used to go back to group fragment
        val backBtn = view.findViewById<Button>(R.id.button4)
        backBtn.setOnClickListener {
            val fragment = GroupFragment()
            val bundle = Bundle()
            bundle.putString("username", username)
            fragment.arguments = bundle
            replaceFragment(fragment)

        }

    return view
    }

    private fun insertGroup(groupName1: String, groupDescription1: String) {

        db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener { userit ->
            if (userit.isEmpty) {
                // If the user does not exist
                Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
            }else{
                for (userdocument in userit){
                    val iD = userdocument.data["UserID"].toString()

                    val iDGroup = Random.nextInt(123568, 9999999)
                    val group = hashMapOf(
                        "GroupID" to iDGroup,
                        "GroupName" to groupName1,
                        "GroupDescription" to groupDescription1,
                        "UserID" to iD
                    )

                   db.collection("Group").whereEqualTo("GroupName", groupName1).whereEqualTo("UserID",iD).get().addOnSuccessListener { groupit ->
                       if (groupit.isEmpty) {


                           val localDate = LocalDate.now()

                           // Hashmap for the member
                           val memberstats = hashMapOf(
                                 "GroupID" to iDGroup,
                                 "UserID" to iD,
                                 "Joined Date" to localDate.toString(),
                                 "Role" to "Group_Admin",
                           )
                           // Add the Group and the member who created
                           db.collection("Group").add(group)
                           db.collection("GroupMembers").add(memberstats)
                           db.collection("ProgresssUser").whereEqualTo("UserID",iD).get().addOnSuccessListener { progressit ->
                               // Check if the user has any progress
                               val xpData = Random.nextInt(100, 1000)
                               if(progressit.isEmpty){

                                   // Call the create xp method to create new progress for the user.
                                   createXp(iD, xpData)
                               }

                               // If the user has progress
                               else{

                                   // Update the user progress
                                   for (document in progressit){

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

                                       titleText.text = buildString {
                                        append("Done Creating Group")
                                    }
                                       messageText.text = buildString {
                                        append("You have gained ")
                                        append(xpData)
                                        append(" xp for creating a group")
                                    }

                                       val dialog = builder.setView(dialogLayout).create() // Create AlertDialog instance

                                       button.setOnClickListener {
                                           // Handle button click here
                                           dialog.dismiss()
                                       }

                                       dialog.show() // Show the dialog

                                       // Update the user progress in the database and add it to the existing progress
                                       val xp = document.get("ProgressXp").toString().toInt()
                                       val updatedXP:Int = xp + xpData

                                       // Update the user progress
                                       db.collection("ProgresssUser").document(progressit.documents[0].id).update("ProgressXp", updatedXP)

                                       // clear the text fields
                                       groupName.text.clear()
                                       groupDescription.text.clear()
                                       progressLoading.dismiss()

                                       // This will  load to update the stats
                                       val activity = activity as MainActivity
                                       activity.loadUserStats()
                                       Toast.makeText(context, "Task Inserted", Toast.LENGTH_SHORT).show()
                                   }
                               }
                           }
                       }else{
                           // If the group already exists
                            Toast.makeText(context, "Group Already Exists", Toast.LENGTH_SHORT).show()
                            progressLoading.dismiss()
                       }
                   }
                }
            }
        }

    }

    // This Method is used to create xp for the user
    private fun createXp (iD: String, xpData: Int) {

        // Hashmap for progress
        val progressXp = hashMapOf(
            "UserID" to iD,
            "ProgressXp" to xpData,

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

            titleText.text = buildString {
                append("Done Creating Group")
            }
            messageText.text = buildString {
                append("You have gained ")
                append(xpData)
                append(" xp for creating a group")
            }

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
            progressLoading.dismiss()
        }
    }

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
         * @return A new instance of fragment CreateGroup.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateGroup().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}