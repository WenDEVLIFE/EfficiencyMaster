package com.example.efficiencymaster

import adapters.TaskAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import classes.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.Locale
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InvidividualTask.newInstance] factory method to
 * create an instance of this fragment.
 */
class InvidividualTask : Fragment(), TaskAdapter.OnCancelListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: TaskAdapter
    var taskList = mutableListOf<Task>()
    var username = ""
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_invidividual_task, container, false)
        val ImageButton = view.findViewById<ImageButton>(R.id.imageButton)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
        }

        ImageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.OpenDrawer()

        }

        // This is for search functions
        val searchView = view.findViewById<SearchView>(R.id.search_group)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                var search: String = query?.lowercase(Locale.getDefault()) ?: return false
                val temp = ArrayList<Task>() // filtered list

                // This will filter the task list
                for (task in taskList) {

                    // get the lowercase and uppercase
                    if (task.taskname.lowercase(Locale.getDefault()).contains(search) || task.taskname.uppercase(Locale.getDefault()).contains(search)) {
                        temp.add(task)
                    }
                }
                adapter.updateList(temp)
                // Add your search logic here
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var search: String = newText?.lowercase(Locale.getDefault()) ?: return false
                val temp = ArrayList<Task>() //  filter  list

                // This will filter the task list
                for (task in taskList){

                    // get the lowercase and uppercase
                    if (task.taskname.lowercase(Locale.getDefault()).contains(search) || task.taskname.uppercase(Locale.getDefault()).contains(search)){
                        temp.add(task)
                    }
                }
                // Add your search logic here
                return true
            }
        })

        // This will get the recycler view from the fragment_group.xml layout
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        taskList = ArrayList()
        adapter = TaskAdapter(taskList)
        recyclerView.adapter = adapter
        adapter.setOnCancelListener(::onCancel)
        LoadTask()

        // Floating  buton action for creation
        val FloatingActionButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        FloatingActionButton.setOnClickListener {
            // Open the drawer when the ImageButton is clicked
            val createTask = CreateTask_Fragment()
            val bundle = Bundle()
            bundle.putString("username", username)
            createTask.arguments = bundle
            ReplaceFragment(createTask)
        }

        return view
    }

    // Get the replace fragment
    fun ReplaceFragment(fragment: Fragment) {
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
         * @return A new instance of fragment InvidividualTask.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InvidividualTask().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // This  will Load the task from the database
    fun LoadTask() {
        db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener {
            for (document in it) {
                val UserID = document.data["UserID"].toString()

                db.collection("Task").whereEqualTo("UserID", UserID).get().addOnSuccessListener {
                    for (document in it) {
                        val Taksname = document.data["TaskName"].toString()
                        val TaskDescription = document.data["TaskDescription"].toString()
                        val Status = document.data["Status"].toString()

                        if (Status.equals("Pending")) {
                            val task = Task(Taksname, TaskDescription)
                            taskList.add(task)
                        }

                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }


    }

    override fun onCancel(position: Int) {
        val TaskName = taskList[position].taskname
        val TaskDescription = taskList[position].taskdescription

        // get the user id
        db.collection("User").whereEqualTo("username",username).get().addOnSuccessListener {
            for (document in it) {

                // Get the user id
                val UserID = document.data["UserID"].toString()

                // get the task  name and its  description
                db.collection("Task").whereEqualTo("TaskName", TaskName).whereEqualTo("TaskDescription", TaskDescription).whereEqualTo("UserID", UserID).get().addOnSuccessListener {
                    for (document in it) {
                        val docId = document.id

                        // Update the status to done
                        db.collection("Task").document(docId).update("Status", "Done")
                            .addOnSuccessListener {
                                Log.d("Firestore", "DocumentSnapshot successfully updated!")
                                Toast.makeText(context, "Task Completed", Toast.LENGTH_SHORT).show()
                                taskList.removeAt(position)
                                adapter.notifyDataSetChanged()
                            }

                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error updating document", e)
                                Toast.makeText(context, "Task Not Completed", Toast.LENGTH_SHORT).show()
                            }


                    }

                    db.collection("ProgresssUser").whereEqualTo("UserID",UserID).get().addOnSuccessListener {
                        val XpData = Random.nextInt(100,  1000) //  Generate random xp

                        // if the document is empty, it will create a new one.
                        if(it.isEmpty){
                            CreateXp(UserID, XpData)

                            // else it will just update the xp
                        }   else{
                            for (document in it){

                                //customize dialogs here below
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

                                // get the current xp and update it
                                val xp = document.get("ProgressXp").toString().toInt()

                                // add
                                val UpdatedXP:Int = xp + XpData

                                db.collection("ProgresssUser").document(it.documents[0].id).update("ProgressXp", UpdatedXP)
                            }
                        }
                    }

                }

            }
        }

    }

     // Method used to create Xp for the user
    fun CreateXp (ID: String, XpData: Int) {


        // This will create a hashmap of the progress
        val progressXp = hashMapOf(
            "UserID" to ID,
            "ProgressXp" to XpData,

            )

        // This will add the progress to the database
        db.collection("ProgresssUser").add(progressXp).addOnSuccessListener {


            // customize alertdialog below
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
            Toast.makeText(context, "Task Done", Toast.LENGTH_SHORT).show()
        }
    }

}