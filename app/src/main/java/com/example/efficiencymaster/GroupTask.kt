package com.example.efficiencymaster

import adapters.GroupTaskAdapter
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import classes.GroupTaskInfo
import classes.NonInterceptingLinearLayoutManager
import com.bumptech.glide.Glide
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GroupTask.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupTask : Fragment(), GroupTaskAdapter.OnCancelListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var fragment:Fragment
    private lateinit var bundle:Bundle
    private lateinit var adapters: GroupTaskAdapter
    private lateinit var progressLoading: ProgressDialog
    val db = Firebase.firestore
    private val networkManager = NetworkManager()

    var username = ""
    private var groupNameIntent = ""
    private var grouptaskList = mutableListOf<GroupTaskInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username  =  it.getString("username").toString()
            groupNameIntent  =  it.getString("groupName").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // This will check if user is connected to the internet.
        networkManager.checkNetworkAndExitIfNotAvailable(requireContext())

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_grouptask, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username  =  it.getString("username").toString()
            groupNameIntent  =  it.getString("groupName").toString()
        }

        // This will Open the drawer when the ImageButton is clicked
        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.openDrawer()

        }


        // This is for search functions
        val searchView = view.findViewById<SearchView>(R.id.search_group)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val search: String = query?.lowercase(Locale.getDefault()) ?: return false
               val temp = ArrayList<GroupTaskInfo>() // filtered list

                // This will filter the task list
                for (task in grouptaskList) {

                    // get the lowercase and uppercase
                    if (task.taskname.lowercase(Locale.getDefault()).contains(search) || task.taskname.uppercase(
                            Locale.getDefault()).contains(search)) {
                        temp.add(task)
                    }
                }
                adapters.updateList(temp)
                // Add your search logic here
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val search: String = newText?.lowercase(Locale.getDefault()) ?: return false
                val temp = ArrayList<GroupTaskInfo>() //  filter  list

                // This will filter the task list
                for (task in grouptaskList){

                    // get the lowercase and uppercase
                    if (task.taskname.lowercase(Locale.getDefault()).contains(search) || task.taskname.uppercase(
                            Locale.getDefault()).contains(search)){
                        temp.add(task)
                    }
                }
                // Add your search logic here
                adapters.updateList(temp)
                return true
            }
        })

        // This is for floating action button
        val fabMenu = view.findViewById<FloatingActionMenu>(R.id.fab_menu)
        val fabOption1 = view.findViewById<FloatingActionButton>(R.id.fab_option1)
        val fabOption2 = view.findViewById<FloatingActionButton>(R.id.fab_option2)
        val fabOption3 = view.findViewById<FloatingActionButton>(R.id.fab_option3)
        val fabOption4 = view.findViewById<FloatingActionButton>(R.id.fab_option4)
        val fabOption5 = view.findViewById<FloatingActionButton>(R.id.fab_option5)

        fabOption1.setOnClickListener {
            // Handle option 1 click
            fabMenu.close(true)
            Toast.makeText(context, "Add Group Task", Toast.LENGTH_SHORT).show()
            db.collection("Group").whereEqualTo("GroupName", groupNameIntent).get().addOnSuccessListener { groupit ->
                if (groupit.isEmpty) {
                    Toast.makeText(context, "Group does not exist", Toast.LENGTH_SHORT).show()
                } else {
                    for (groupdoc in groupit) {
                        val groupid = groupdoc.data["GroupID"].toString()
                        val groupids = Integer.parseInt(groupid)
                        db.collection("GroupMembers")
                            .whereEqualTo("GroupID", groupids)
                            .whereEqualTo("UserID", username).get()
                            .addOnSuccessListener { groupmemberit ->
                                if (groupmemberit.isEmpty) {
                                    Toast.makeText(
                                        context,
                                        "User is not a member of the group",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    for (groupmemberdoc in groupmemberit) {
                                        val groupRole = groupmemberdoc.data["Role"].toString()
                                        if (groupRole == "Group_Admin") {
                                            fragment = CreateGroupTask()
                                            bundle = Bundle()
                                            bundle.putString("username", username)
                                            bundle.putString("groupName", groupNameIntent)
                                            fragment.arguments = bundle
                                            replaceFragment(fragment)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "You are not allowed to edit the group",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                    }
                }

            }

        }

        fabOption2.setOnClickListener {
            // Handle option 2 click
            fabMenu.close(true)

          db.collection("Group").whereEqualTo("GroupName", groupNameIntent).get().addOnSuccessListener { groupit ->
              if (groupit.isEmpty) {
                  Toast.makeText(context, "Group does not exist", Toast.LENGTH_SHORT).show()
              } else {
                  for (groupdoc in groupit) {
                      val groupid = groupdoc.data["GroupID"].toString()
                      val groupids = Integer.parseInt(groupid)
                      db.collection("GroupMembers")
                          .whereEqualTo("GroupID", groupids)
                          .whereEqualTo("UserID", username).get()
                          .addOnSuccessListener { groupmemberit ->
                              if (groupmemberit.isEmpty) {
                                  Toast.makeText(
                                      context,
                                      "User is not a member of the group",
                                      Toast.LENGTH_SHORT
                                  ).show()
                              } else {
                                  for (groupmemberdoc in groupmemberit) {
                                      val groupRole = groupmemberdoc.data["Role"].toString()
                                      if (groupRole == "Group_Admin") {
                                          fragment = Members()
                                          bundle = Bundle()
                                          bundle.putString("username", username)
                                          bundle.putString("groupName", groupNameIntent)
                                          fragment.arguments = bundle
                                          replaceFragment(fragment)
                                      } else {
                                          Toast.makeText(
                                              context,
                                              "You are not allowed to edit the group",
                                              Toast.LENGTH_SHORT
                                          ).show()
                                      }
                                  }
                              }
                          }
                  }
              }

          }

            Toast.makeText(context, "View Group Members", Toast.LENGTH_SHORT).show()
        }


        fabOption3.setOnClickListener {
            // Handle option 3 click
            fabMenu.close(true)
            // This will go to MembersPending.kt 👾
            db.collection("Group").whereEqualTo("GroupName", groupNameIntent).get().addOnSuccessListener { groupit ->
                if (groupit.isEmpty) {
                    Toast.makeText(context, "Group does not exist", Toast.LENGTH_SHORT).show()
                } else {
                    for (groupdoc in groupit) {
                        val groupid = groupdoc.data["GroupID"].toString()
                        val groupids = Integer.parseInt(groupid)
                        db.collection("GroupMembers")
                            .whereEqualTo("GroupID", groupids)
                            .whereEqualTo("UserID", username).get()
                            .addOnSuccessListener { groupmemberit ->
                                if (groupmemberit.isEmpty) {
                                    Toast.makeText(
                                        context,
                                        "User is not a member of the group",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    for (groupmemberdoc in groupmemberit) {
                                        val groupRole = groupmemberdoc.data["Role"].toString()
                                        if (groupRole == "Group_Admin") {
                                            fragment = PendingMembers()
                                            bundle = Bundle()
                                            bundle.putString("username", username)
                                            bundle.putString("groupName", groupNameIntent)
                                            fragment.arguments = bundle
                                            replaceFragment(fragment)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "You are not allowed to edit the group",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                    }
                }

            }
            Toast.makeText(context, "View Pending Members", Toast.LENGTH_SHORT).show()
        }

        fabOption4.setOnClickListener {
            // Handle option 4 click
            fabMenu.close(true)
            Toast.makeText(context, "View Done Task", Toast.LENGTH_SHORT).show()
        }

        fabOption5.setOnClickListener {
            // Handle option 5 click
            fabMenu.close(true)
            // This will go to joined group fragment 👾
            fragment = YourJoinedGroup()
            bundle = Bundle()
            bundle.putString("username", username)
            fragment.arguments = bundle
            replaceFragment(fragment)
        }

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setLayoutManager(LinearLayoutManager(requireContext()))
        grouptaskList = ArrayList()
        adapters =  GroupTaskAdapter(grouptaskList)
        recyclerView.adapter = adapters
        adapters.setOnCancelListener(::onCancel)
        loadGroupTask()

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

    // This will load the group
    @SuppressLint("NotifyDataSetChanged")
    private fun loadGroupTask(){

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

                                // Load the task and add on the list
                                val groupTaskInfo = GroupTaskInfo(
                                    "Task:$taskname",
                                    "Details:$details", "Status:$status", "Assigned to:$assigned", "Created by:$createdBy")
                                grouptaskList.add(groupTaskInfo)
                            }

                            // Notify the adapter
                            adapters.notifyDataSetChanged()
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
         * @return A new instance of fragment YourTask.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroupTask().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCancel(position: Int) {
        // Get the taskname from the list
        val taskName = grouptaskList[position].taskname

        val tasknameSubString = taskName.removePrefix("Task:")

        // cuztomize alert dialog component below
        val builder = android.app.AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.message_layout2, null)
        val titleText = dialogLayout.findViewById<TextView>(R.id.dialog_title)
        val messageText = dialogLayout.findViewById<TextView>(R.id.dialog_message)
        val button = dialogLayout.findViewById<Button>(R.id.dialog_button)
        button.text = buildString {
            append("Delete")
        }
        val button2 = dialogLayout.findViewById<Button>(R.id.dialog_button2)
        val imageView1 = dialogLayout.findViewById<ImageView>(R.id.imageView2)

        Glide.with(requireContext())
            .asGif()
            .load(R.drawable.confused)
            .into(imageView1)
        imageView1.scaleType = ImageView.ScaleType.FIT_CENTER
        val params = imageView1.layoutParams
        val scale = resources.displayMetrics.density
        params.width = (100 * scale).toInt()
        params.height = (100 * scale).toInt()
        imageView1.layoutParams = params
        titleText.text = buildString {
            append("Delete the task")
        }
        messageText.text = buildString {
            append("Are you sure you want to delete the task?")
            append(taskName)
            append("?")
        }

        val dialog = builder.setView(dialogLayout).create()

        dialog.show()

        button.setOnClickListener {

            // Load the progress dialog
            progressLoading = ProgressDialog(requireContext())
            progressLoading.setTitle("Deleting Task...")
            progressLoading.setMessage("Please wait...")
            progressLoading.setCanceledOnTouchOutside(false)
            progressLoading.show()

            // Find the Group Name in the group collection
            db.collection("Group").whereEqualTo("GroupName", groupNameIntent).get().addOnSuccessListener { groupit->
                if (groupit.isEmpty) {
                    Toast.makeText(context, "Group does not exist", Toast.LENGTH_SHORT).show()
                    progressLoading.dismiss()
                    }
                else{

                    // else it will load the group id
                    for  (groupdoc in groupit){

                        // Get the group id
                        val groupid = groupdoc.data["GroupID"].toString().toInt()

                        // Get the username from the user collection
                        db.collection("User").whereEqualTo("username",username).get().addOnSuccessListener { userit ->
                            if (userit.isEmpty){
                                Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
                                progressLoading.dismiss()
                            }

                            //  else it will load the user id
                            else{
                                 for (userdoc in userit){

                                     // get the  user id
                                     val userid = userdoc.data["UserID"].toString()

                                     //  Find the group member where equals to group id and user id
                                     db.collection("GroupMembers")
                                         .whereEqualTo("GroupID",groupid)
                                         .whereEqualTo("UserID", userid)
                                         .get().addOnSuccessListener {
                                                groupmemberit ->
                                                if (groupmemberit.isEmpty){
                                                    Toast.makeText(context, "User is not a member of the group", Toast.LENGTH_SHORT).show()
                                                    progressLoading.dismiss()
                                                }

                                                // else it will load the group documents info
                                                else{
                                                    for (groupmemberdoc in groupmemberit){

                                                        // Get the role
                                                        val groupRole = groupmemberdoc.data["Role"].toString()

                                                        if(groupRole == "Group_Admin"){
                                                            // Find the taskname in task collection
                                                            db.collection("Task").whereEqualTo("TaskName",tasknameSubString).get().addOnSuccessListener { taskit ->

                                                                // This  will error if the task is empty
                                                                if (taskit.isEmpty){
                                                                    Toast.makeText(context, "Task does not exist", Toast.LENGTH_SHORT).show()
                                                                    progressLoading.dismiss()
                                                                }else{

                                                                    // Delete the task
                                                                    for (taskdocument in taskit) {
                                                                        val taskid = taskdocument.id
                                                                        db.collection("Task").document(taskid).delete().addOnSuccessListener {

                                                                            // Call the method success then remove the value from the task and update the recycleviewer adapter and dismiss the dialog
                                                                            success()
                                                                            grouptaskList.removeAt(position)
                                                                            adapters.notifyDataSetChanged()
                                                                            dialog.dismiss()
                                                                            progressLoading.dismiss()
                                                                        }.addOnFailureListener {
                                                                            Toast.makeText(context, "Task not deleted", Toast.LENGTH_SHORT).show()
                                                                            progressLoading.dismiss()
                                                                        }
                                                                    }
                                                                }

                                                            }.addOnFailureListener {
                                                                Toast.makeText(context, "Task not deleted", Toast.LENGTH_SHORT).show()
                                                                progressLoading.dismiss()
                                                            }
                                                        }else{
                                                            Toast.makeText(context, "You are not allowed to delete the task", Toast.LENGTH_SHORT).show()
                                                            progressLoading.dismiss()
                                                        }


                                                    }
                                                }
                                         }
                                }
                            }


                        }

                    }

                }

            }


        }

        button2.setOnClickListener{
            dialog.dismiss()
        }
    }

    //This method used for success
    private fun success(){
        // below are the customize alert dialgo components and etc.
        val builder1 = android.app.AlertDialog.Builder(context)
        val inflater1 = layoutInflater
        val dialogLayout1 = inflater1.inflate(R.layout.message_layout, null)
        val titleText1= dialogLayout1.findViewById<TextView>(R.id.dialog_title)
        val messageText1 = dialogLayout1.findViewById<TextView>(R.id.dialog_message)
        val button1 = dialogLayout1.findViewById<Button>(R.id.dialog_button)
        button1.text = buildString {
            append("Ok")
        }
        val imageView2 = dialogLayout1.findViewById<ImageView>(R.id.imageView2)

        Glide.with(requireContext())
            .asGif()
            .load(R.drawable.alert)
            .into(imageView2)
        imageView2.scaleType = ImageView.ScaleType.FIT_CENTER
        val params1 = imageView2.layoutParams
        val scale1 = resources.displayMetrics.density
        params1.width = (100 * scale1).toInt()
        params1.height = (100 * scale1).toInt()
        imageView2.layoutParams = params1
        titleText1.text = buildString {
            append("Task Alert")
        }
        messageText1.text = buildString {
            append("Task Delete Successfully .")
        }

        val dialog1 = builder1.setView(dialogLayout1).create()

        dialog1.show()
        button1.setOnClickListener{
            dialog1.dismiss()
        }
    }


}