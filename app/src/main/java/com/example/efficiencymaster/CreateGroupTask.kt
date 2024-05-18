package com.example.efficiencymaster

import android.animation.AnimatorInflater
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
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
    private lateinit var taskName: EditText
    private lateinit var taskdescription: EditText
    private lateinit var progressLoading: ProgressDialog
    private lateinit var groupmemberSpinner:Spinner

    val db = Firebase.firestore
    var username =""
    private var groupName =""
    private var memberList =ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            groupName = it.getString("groupName").toString()
            username = it.getString("username").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_group_task, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            groupName = it.getString("groupName").toString()
            username = it.getString("username").toString()
        }

        // Our imagebutton
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.openDrawer()

        }

            // Our edit text
        taskName = view.findViewById(R.id.editTextText2)
        taskdescription = view.findViewById(R.id.desscripts)

        // Get the spinner
        groupmemberSpinner = view.findViewById(R.id.spinner)
        memberList.add("Select a member")
        loadMember() // Load the member of the group

        // adapter for spinner
        val adapter = ArrayAdapter(requireContext(), R.layout.spinnerlayout, memberList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter to the spinner
        groupmemberSpinner.adapter = adapter

        // Set the default selection
        groupmemberSpinner.setSelection(0)

        val createTaskBtn = view.findViewById<Button>(R.id.button2)
        createTaskBtn.setOnClickListener {
            val taskname1 = taskName.text.toString()
            val taskdescription1 = taskdescription.text.toString()
            val memberName = groupmemberSpinner.selectedItem.toString()

            if(taskname1.isEmpty()) {
                taskName.error = "Please Enter Task Name"
            }else{
                if(taskdescription1.isEmpty()) {
                    this.taskdescription.error = "Please Enter Task Description"
                }else{

                    if (memberName == "Select a member"){
                        Toast.makeText(context, "Please Select a member", Toast.LENGTH_SHORT).show()
                        }
                     else{
                        insertTask(taskname1, taskdescription1, memberName)

                        // Load the progressdialog when the task is being inserted
                        Suppress("DEPRECATION")
                        progressLoading= ProgressDialog(requireContext())
                        progressLoading.setTitle("Inserting Group Task")
                        progressLoading.setMessage("Inserting Group Task Please Wait..")
                        progressLoading.setCanceledOnTouchOutside(false)
                        progressLoading.show()
                    }
                }
            }
        }


    return view
    }

    // This method is used to Insert the Task 👌
    private fun insertTask(taskname1: String, taskDescription1: String, memberName: String) {

        db.collection("User").whereEqualTo("username",memberName).get().addOnSuccessListener { userit ->
            for (document in userit){
                val iD = document.data["UserID"].toString()
                val userRetrieve = document.data["username"].toString()

                // This will insert the task into the Firestore database
                val task = hashMapOf(
                    "UserID" to iD,
                    "TaskName" to taskname1,
                    "TaskDescription" to taskDescription1,
                    "Status" to "Pending",
                    "UserID" to iD,
                    "CreatedBy" to username,
                    "AssignedTo" to userRetrieve,
                    "Type" to "Group",
                )

                val xpData = Random.nextInt(100,  1000)

                // Check if the task already exists in the database
                db.collection("Task").whereEqualTo("TaskName", taskname1).whereEqualTo("UserID",iD).get().addOnSuccessListener { taskit->
                    if(taskit.isEmpty){

                        // Insert the task into the database
                        db.collection("Task").add(task).addOnSuccessListener {
                            taskName.text.clear()
                            taskdescription.text.clear()
                            progressLoading.dismiss()
                            Toast.makeText(context, "Group Task Inserted", Toast.LENGTH_SHORT).show()
                        }

                        db.collection("ProgresssUser").whereEqualTo("UserID",iD).get().addOnSuccessListener { progressit ->
                            // Check if the user has any progress
                            if(progressit.isEmpty){

                                // Call the create xp method to create new progress for the user.
                                createXp(iD, xpData)
                            }

                            // If the user has progress
                            else{

                                // Update the user progress
                                for (progressdocument in progressit){

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
                                        append("Done Creating Group Task")
                                    }
                                    messageText.text = buildString {
                                        append("You have gained ")
                                        append(xpData)
                                        append(" xp for creating a group task")
                                    }

                                    val dialog = builder.setView(dialogLayout).create() // Create AlertDialog instance

                                    button.setOnClickListener {
                                        // Handle button click here
                                        dialog.dismiss()
                                    }

                                    dialog.show() // Show the dialog

                                    // Update the user progress in the database and add it to the existing progress
                                    val xp = progressdocument.get("ProgressXp").toString().toInt()
                                    val updatedXP:Int = xp + xpData

                                    // Update the user progress
                                    db.collection("ProgresssUser").document(progressit.documents[0].id).update("ProgressXp", updatedXP)

                                    // clear the text fields
                                    taskName.text.clear()
                                    taskdescription.text.clear()
                                    progressLoading.dismiss()
                                    groupmemberSpinner.setSelection(0)
                                    Toast.makeText(context, "Task Inserted", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }else{
                        taskName.error = "Task Already Exists"
                        progressLoading.dismiss()
                    }
                }
            }
        }
    }


    // This method is used to create a new progress for the user
    private fun createXp (iD: String, xpData: Int) {

        // Hashmap for progress
        val progressXp = hashMapOf(
            "UserID" to iD,
            "ProgressXp" to xpData,

            )

        // Add the progress to the database
        db.collection("ProgresssUser").add(progressXp).addOnSuccessListener {
            // This will  load to update the stats
            val activity = activity as MainActivity
            activity.loadUserStats()
            // Customize dialog below here
            val builder = AlertDialog.Builder(context)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.message_layout, null)

            val titleText = dialogLayout.findViewById<TextView>(R.id.dialog_title)
            val messageText = dialogLayout.findViewById<TextView>(R.id.dialog_message)
            val button = dialogLayout.findViewById<Button>(R.id.dialog_button)

            titleText.text = buildString {
                append("Done Creating Group Task")
            }
            messageText.text = buildString {
                append("You have gained ")
                append(xpData)
                append(" xp for creating a group task")
            }

            val dialog = builder.setView(dialogLayout).create() // Create AlertDialog instance
            button.setOnClickListener {
                // Handle button click here

                dialog.dismiss()

            }

            builder.setView(dialogLayout)
            builder.show()

            // clear the text fields
            taskName.text.clear()
            taskdescription.text.clear()
            progressLoading.dismiss()
            groupmemberSpinner.setSelection(0)
            Toast.makeText(context, "Task Inserted", Toast.LENGTH_SHORT).show()
        }
    }

    //  This will load the member from the list
    private fun loadMember(){
        db.collection("Group").whereEqualTo("GroupName",groupName).get().addOnSuccessListener { groupit->
            if (groupit.isEmpty) {
                Toast.makeText(context, "Group does not exist", Toast.LENGTH_SHORT).show()
            }else{
                for (document in groupit){

                    // Then get the GroupID
                    val groupID= document.data["GroupID"].toString()

                    // Get the user id from the group members
                    db.collection("GroupMembers").get().addOnSuccessListener { memberit->
                      if (memberit.isEmpty) {
                          Toast.makeText(context, "No member in the group", Toast.LENGTH_SHORT)
                              .show()
                      }
                        else{
                          for (memberdocument in memberit){

                              val groupid = memberdocument.data["GroupID"].toString()

                              if (groupid == groupID){
                                  // Get the user id from the group members
                                  val userid = memberdocument.data["UserID"].toString()

                                  // Then get the username by the user id
                                  db.collection("User").whereEqualTo("UserID",userid).get().addOnSuccessListener { userit->
                                      if (userit.isEmpty) {
                                          Toast.makeText(
                                              context,
                                              "No member in the group",
                                              Toast.LENGTH_SHORT
                                          )
                                              .show()
                                      }else{
                                          for (userdocument in userit){

                                              // Once we get the username we will now add it on the list
                                              memberList.add(userdocument.data["username"].toString())
                                          }
                                      }
                                  }.addOnFailureListener {
                                      Toast.makeText(context, "Error loading member", Toast.LENGTH_SHORT).show()
                                  }
                              }
                          }
                      }
                    }.addOnFailureListener {
                        Toast.makeText(context, "Error loading member", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error loading member", Toast.LENGTH_SHORT).show()
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