package com.example.efficiencymaster

import adapters.groupDoneTaskAdapter
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import classes.GroupTaskInfo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GroupDoneTask.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupDoneTask : Fragment(),groupDoneTaskAdapter.OnDeleteListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recycleView:RecyclerView
    private lateinit var circularProgressBar : CircularProgressBar
    private lateinit var circularProgressBar2 : CircularProgressBar
    private lateinit var adapter:groupDoneTaskAdapter
    private lateinit var percentage : TextView
    private lateinit var taskDone : TextView
    private var retriveCount1:Double = 0.00
    private var retriveCount2:Double = 0.00
    val db = Firebase.firestore


    var groupNameIntent = ""
    var username =""
    var groupTaskList = mutableListOf<GroupTaskInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
            groupNameIntent = it.getString("groupName").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_group_done_task, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Replace with your previous fragment here
                val fragment = GroupTask()
                val bundle = Bundle()
                bundle.putString("username", username)
                bundle.putString("groupName", groupNameIntent)
                fragment.arguments = bundle
                replaceFragment((fragment))
            }
        })

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
            groupNameIntent = it.getString("groupName").toString()
        }

        // This will Open the drawer when the ImageButton is clicked
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.openDrawer()

        }
        taskDone = view.findViewById(R.id.done_task_count)
        percentage = view.findViewById(R.id.textView5)

        // This is for search functions
        val searchView = view.findViewById<SearchView>(R.id.search_group)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val search: String = query?.lowercase(Locale.getDefault()) ?: return false
                val temp = ArrayList<GroupTaskInfo>() // filtered list

                // This will filter the task list
                for (task in groupTaskList) {

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
                val search: String = newText?.lowercase(Locale.getDefault()) ?: return false
                val temp = ArrayList<GroupTaskInfo>() //  filter  list

                // This will filter the task list
                for (task in groupTaskList){

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

        circularProgressBar = view.findViewById(R.id.circularProgressBar)
        circularProgressBar.apply {
            // Set Progress
            //progress = 100f
            // or with animation
            //setProgressWithAnimation(100f, 3000) // =1s

            // Set Progress Max
            progressMax = 100f

            // Set ProgressBar Color
            progressBarColor = Color.GREEN
            // or with gradient
            progressBarColorStart = Color.GREEN
            progressBarColorEnd = Color.GREEN
            progressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            // Set background ProgressBar Color
            backgroundProgressBarColor = Color.GREEN
            // or with gradient
            backgroundProgressBarColorStart = Color.WHITE
            backgroundProgressBarColorEnd = Color.WHITE
            backgroundProgressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            // Set Width
            progressBarWidth = 7f // in DP
            backgroundProgressBarWidth = 3f // in DP

            // Other
            roundBorder = true
            startAngle = 180f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
        }
        circularProgressBar2 = view.findViewById(R.id.circularProgressBar2)
        circularProgressBar2.apply {
            // Set Progress
            //progress = 100f
            // or with animation
            //setProgressWithAnimation(100f, 3000) // =1s
            // Set Progress Max
            progressMax = 100f

            // Set ProgressBar Color
            progressBarColor = Color.GREEN
            // or with gradient
            progressBarColorStart = Color.GREEN
            progressBarColorEnd = Color.GREEN
            progressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            // Set background ProgressBar Color
            backgroundProgressBarColor = Color.GREEN
            // or with gradient
            backgroundProgressBarColorStart = Color.WHITE
            backgroundProgressBarColorEnd = Color.WHITE
            backgroundProgressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            // Set Width
            progressBarWidth = 7f // in DP
            backgroundProgressBarWidth = 3f // in DP

            // Other
            roundBorder = true
            startAngle = 180f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
        }
        loadStats()


        recycleView = view.findViewById(R.id.recycler_view)
        recycleView.setLayoutManager(LinearLayoutManager(context))
        groupTaskList = ArrayList()
        adapter = groupDoneTaskAdapter(groupTaskList)
        recycleView.adapter = adapter
        adapter.setOnDeleteListener(::onDelete)
        loadTask()


        return view
    }

    private fun loadTask(){
        // Find the  group name on the group collection
        db.collection("Group").whereEqualTo("GroupName", groupNameIntent).get().addOnSuccessListener { grouptaskit ->
            if (grouptaskit.isEmpty) {
                Toast.makeText(context, "Group does not exist", Toast.LENGTH_SHORT).show()
            }else{
                //  Then if found it  will load the group id
                for (groupdocument in grouptaskit){

                    // get the group id from the database
                    val groupid =  groupdocument.data["GroupID"].toString()

                    // Find the task group where equals to group id
                    db.collection("Task").whereEqualTo("GroupID", groupid).get().addOnSuccessListener { taskit ->
                        if (taskit.isEmpty) {
                            Toast.makeText(context, "Task does not exist", Toast.LENGTH_SHORT).show()
                        }else{

                            // Then load the task details
                            for (taskdocument in taskit){
                                val taskname = taskdocument.data["TaskName"].toString()
                                val details = taskdocument.data["TaskDescription"].toString()
                                val status = taskdocument.data["Status"].toString()
                                val assigned = taskdocument.data["AssignedTo"].toString()
                                val createdBy = taskdocument.data["CreatedBy"].toString()

                                // Check if the status is done
                                if  (status == "Done"){

                                    // Load the task and add on the list
                                    val groupTaskInfo = GroupTaskInfo(
                                        "Task:$taskname",
                                        "Details:$details", "Status:$status", "Assigned to:$assigned", "Created by:$createdBy")
                                    groupTaskList.add(groupTaskInfo)
                                }
                            }

                            // Notify the adapter
                            adapter.notifyDataSetChanged()
                        }
                    }



                }

            }
        }
    }

    // Load the stats of the group
    private fun loadStats(){

        // Find the group name on the group collection
      db.collection("Group").whereEqualTo("GroupName",groupNameIntent).get().addOnSuccessListener { groupit->
          for (group in groupit){


              // Get the group id
              val groupid = group.data["GroupID"].toString()

              db.collection("Task").whereEqualTo("GroupID",groupid).get().addOnSuccessListener { taskit->
                  for (task in taskit){
                      // Collection Reference
                      val collectionReference1 = db.collection("Task")

                      // Get the query of the Pending task for the specific user
                      val query1 = collectionReference1.whereEqualTo("Status", "Pending").whereEqualTo("GroupID", groupid)
                      query1.get().addOnSuccessListener { pendingit ->
                          val pending = pendingit.size()
                          retriveCount1 = pending.toDouble()

                          // Get the query of the Done task for the specific user
                          val query2 = collectionReference1.whereEqualTo("Status", "Done").whereEqualTo("GroupID", groupid)
                          query2.get().addOnSuccessListener {doneit ->
                              val done = doneit.size()
                              retriveCount2 = done.toDouble()

                              val total = retriveCount1 + retriveCount2
                              val percentages1 = retriveCount2 / total * 100
                              val percentages2 = retriveCount1 / total * 100

                              // set to substring 0 to 5
                              val stringg1 = percentages1.toString()
                              val stringg2 = percentages2.toString()
                              val subStr = subString(stringg2)
                              val subStr2 = subString(stringg1)

                              // get the float value of the percentages
                              val floatPercentages1 = percentages1.toFloat()
                              val floatPercentages2 = percentages2.toFloat()

                              // set the text of the percentage
                              percentage.text = "$subStr %"
                              taskDone.text = "$subStr2 %"

                              // set the progress of the circular progress bar
                              circularProgressBar.setProgressWithAnimation(floatPercentages2, 3000)
                              circularProgressBar2.setProgressWithAnimation(floatPercentages1, 3000)
                              // Update the UI here with the calculated percentage
                          }
                      }
                  }
              }

          }

      }
    }

    // Limit the word from 0 to 5 letters only.
    private fun subString (string: String): String {
        return if (string.length <= 5) string else string.substring(0, 5)
    }

    // Method used to Replae the fragment
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
         * @return A new instance of fragment groupDoneTaskAdapter.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroupDoneTask().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDelete(position: Int) {
        TODO("Not yet implemented")
    }
}