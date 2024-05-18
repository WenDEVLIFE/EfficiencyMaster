package com.example.efficiencymaster

import adapters.DoneTaskAdapter
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import classes.DoneTask
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DoneTaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DoneTaskFragment : Fragment(), DoneTaskAdapter.OnCancelListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val db = Firebase.firestore
    lateinit var adapter:DoneTaskAdapter
    lateinit var recycleviewer:RecyclerView
    var taskList = mutableListOf<DoneTask>()
    var username =""
    lateinit var ProgressLoading: ProgressDialog

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
        val view = inflater.inflate(R.layout.fragment_done_task, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
        }

        // Image Button and its ID
        val ImageButton = view.findViewById<ImageButton>(R.id.imageButton)
        ImageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.openDrawer()

        }
        // This is for search functions
        val searchView = view.findViewById<SearchView>(R.id.search_group)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                var search: String = query?.lowercase(Locale.getDefault()) ?: return false

                val temp = ArrayList<DoneTask>() // filtered list

                // This will filter the task list
                for (task in taskList) {

                    // get the lowercase and uppercase
                    if (task.taskname.lowercase(Locale.getDefault()).contains(search) || task.taskname.uppercase(
                            Locale.getDefault()).contains(search)) {
                        temp.add(task)
                    }
                }
                 adapter.updateList(temp)
                // Add your search logic here
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var search: String = newText?.lowercase(Locale.getDefault()) ?: return false

                val temp = ArrayList<DoneTask>() //  filter  list

                // This will filter the task list
                for (task in taskList){

                    // get the lowercase and uppercase
                    if (task.taskname.lowercase(Locale.getDefault()).contains(search) || task.taskname.uppercase(
                            Locale.getDefault()).contains(search)){
                        temp.add(task)
                    }
                }
                // Add your search logic here
                adapter.updateList(temp)

                return true
            }
        })

        // This will get the recycler view from the fragment_group.xml layout
        // and set the layout manager to linear layout manager and set the adapter
        // to the task adapter
        recycleviewer = view.findViewById(R.id.recycler_view)
        recycleviewer.setLayoutManager(LinearLayoutManager(context))
        taskList = ArrayList()
        adapter = DoneTaskAdapter(taskList)
        recycleviewer.adapter=adapter
        adapter.setOnCancelListener(::onCancel)
        LoadTask()


        return view
    }

    // This  will Load the task from the database
    fun LoadTask() {

        // find the username
        db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener {
            for (document in it) {

                // then get the UserID
                val UserID = document.data["UserID"].toString()

                // Then find the username id in the task
                db.collection("Task").whereEqualTo("UserID", UserID).get().addOnSuccessListener {
                    for (document in it) {

                        // Then get the task name, description, status, and completion date.
                        val Taksname = document.data["TaskName"].toString()
                        val TaskDescription = document.data["TaskDescription"].toString()
                        val Status = document.data["Status"].toString()
                        val CompletionDate = document.data["CompletionDate"].toString()

                        // if status is done, it will add on the list and then update the adapter
                        if (Status.equals("Done")) {
                            val task = DoneTask(Taksname, TaskDescription,Status, CompletionDate)
                            taskList.add(task)
                        }

                    }

                    // update the adapter
                    adapter.notifyDataSetChanged()
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
         * @return A new instance of fragment DoneTaskFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DoneTaskFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCancel(position: Int) {

        //  Get the task name from the list
        val taskName = taskList[position].taskname

        // Customize alert dialog below
        val builder = android.app.AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.message_layout2, null)

        val titleText = dialogLayout.findViewById<TextView>(R.id.dialog_title)
        val messageText = dialogLayout.findViewById<TextView>(R.id.dialog_message)
        val button = dialogLayout.findViewById<Button>(R.id.dialog_button)
        val button2 = dialogLayout.findViewById<Button>(R.id.dialog_button2)
        val ImageView = dialogLayout.findViewById<ImageView>(R.id.imageView2)
        ImageView.setImageResource(R.drawable.question_mark)

        titleText.text = "Delete the Task "
        messageText.text = "Are you sure you want to delete the task $taskName?"

        val dialog = builder.setView(dialogLayout).create() // Create AlertDialog instance

        button.setOnClickListener {

            // Load the ProgressDialog
            ProgressLoading = ProgressDialog(context)
            ProgressLoading.setMessage("Deleting Task...")
            ProgressLoading.setMessage("This will take a while..")
            ProgressLoading.setCanceledOnTouchOutside(false)
            ProgressLoading.show()
            // Handle button click here

            // Find the username in the collections
            db.collection("User").whereEqualTo("username",username).get().addOnSuccessListener {
                if (it.isEmpty) {
                    Toast.makeText(context , "User does not exist", Toast.LENGTH_SHORT).show()
                    ProgressLoading.dismiss()
                    return@addOnSuccessListener
                }
                else{

                    // Then load the UserID
                    for (document in it){

                        val UserID = document.data["UserID"].toString()
                        db.collection("Task").whereEqualTo("TaskName", taskName).whereEqualTo("UserID" ,UserID).get().addOnSuccessListener {
                            for (document in it) {
                                val documentid = document.id
                                db.collection("Task").document(documentid).delete()

                                // dismis the dialog and update the list and the adapter.
                                ProgressLoading.dismiss()
                                taskList.removeAt(position)
                                adapter.notifyDataSetChanged()
                                dialog.dismiss()

                                // Customize alert dialog below
                                val builder1 = android.app.AlertDialog.Builder(context)
                                val inflater1 = layoutInflater
                                val dialogLayout1 = inflater1.inflate(R.layout.message_layout, null)

                                val titleText1 = dialogLayout1.findViewById<TextView>(R.id.dialog_title)
                                val messageText1 = dialogLayout1.findViewById<TextView>(R.id.dialog_message)
                                val button1 = dialogLayout1.findViewById<Button>(R.id.dialog_button)
                                val ImageView1 = dialogLayout1.findViewById<ImageView>(R.id.imageView2)
                                ImageView1.setImageResource(R.drawable.check)

                                titleText1.text = "Task Delete Successfully "
                                messageText1.text = "You successfully deleted the $taskName"

                                val dialog1 = builder1.setView(dialogLayout1).create() // Create AlertDialog instance

                                button1.setOnClickListener {
                                    dialog1.dismiss()

                                }

                                dialog1.show()
                            }
                        }
                    }
                }
            }

        }

        button2.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show() // Show the dialog
    }
}