package com.example.efficiencymaster

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
 * Use the [CreateTask_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateTask_Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var TaskName:EditText
    lateinit var TaskDescription: EditText
    lateinit var ProgressLoading: ProgressDialog

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_task_, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
        }

        // Get the id of the text fields
        TaskName = view.findViewById<EditText>(R.id.editTextText2)
        TaskDescription = view.findViewById<EditText>(R.id.desscripts)

        val create_Task_btn = view.findViewById<Button>(R.id.button2)
        create_Task_btn.setOnClickListener {
            val taskname = TaskName.text.toString()
            val taskdescription = TaskDescription.text.toString()

           if(taskname.isEmpty()) {
              TaskName.error = "Please Enter Task Name"
           }else{
               if(taskdescription.isEmpty()) {
                   TaskDescription.error = "Please Enter Task Description"
               }else{

                   InsertTask(taskname, taskdescription)

                   ProgressLoading= ProgressDialog(context)
                   ProgressLoading.setTitle("Inserting Task")
                   ProgressLoading.setMessage("Inserting Task Please Wait..")
                   ProgressLoading.setCanceledOnTouchOutside(false)
                   ProgressLoading.show()
               }
           }
        }

        val ImageButton = view.findViewById<ImageButton>(R.id.imageButton)
        ImageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.OpenDrawer()

        }

        return view
    }

    // This method is used to Insert the Task 👌
    fun InsertTask(taskname: String, taskDescription: String) {

        db.collection("User").whereEqualTo("username",username).get().addOnSuccessListener {
            for (document in it){
                val ID = document.data["UserID"].toString()

                // This will insert the task into the Firestore database
                val task = hashMapOf(
                    "UserID" to ID,
                    "TaskName" to taskname,
                    "TaskDescription" to taskDescription,
                    "Status" to "Pending",
                    "UserID" to ID
                )

                val XpData = Random.nextInt(100,  1000)

                // Check if the task already exists in the database
               db.collection("Task").whereEqualTo("TaskName", taskname).whereEqualTo("UserID",ID).get().addOnSuccessListener {
                   if(it.isEmpty){

                       // Insert the task into the database
                       db.collection("Task").add(task).addOnSuccessListener {
                            TaskName.text.clear()
                            TaskDescription.text.clear()
                           ProgressLoading.dismiss()
                           Toast.makeText(context, "Task Inserted", Toast.LENGTH_SHORT).show()
                       }

                       db.collection("ProgresssUser").whereEqualTo("UserID",ID).get().addOnSuccessListener {
                          // Check if the user has any progress
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

                                   titleText.text = "Done Creating Task"
                                   messageText.text = "You have gained $XpData xp for creating a task"

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
                                   TaskName.text.clear()
                                   TaskDescription.text.clear()
                                   ProgressLoading.dismiss()
                                   Toast.makeText(context, "Task Inserted", Toast.LENGTH_SHORT).show()
                               }
                           }
                       }
                   }else{
                       TaskName.error = "Task Already Exists"
                       ProgressLoading.dismiss()
                   }
               }
            }
        }
    }


    // This method is used to create a new progress for the user
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

            titleText.text = "Done Creating Task"
            messageText.text = "You have gained $XpData xp for creating a task"

            val dialog = builder.setView(dialogLayout).create() // Create AlertDialog instance
            button.setOnClickListener {
                // Handle button click here

                dialog.dismiss()

            }

            builder.setView(dialogLayout)
            builder.show()

            // clear the text fields
            TaskName.text.clear()
            TaskDescription.text.clear()
            ProgressLoading.dismiss()
            Toast.makeText(context, "Task Inserted", Toast.LENGTH_SHORT).show()
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateTask_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateTask_Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}