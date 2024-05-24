package com.example.efficiencymaster

import adapters.CreatedGroupAdapter
import android.annotation.SuppressLint
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
import classes.Group
import classes.GroupTaskInfo
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreatedGroup.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreatedGroup : Fragment(), CreatedGroupAdapter.OnCancelListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adapter:CreatedGroupAdapter
    private lateinit var recyclerView:RecyclerView
    private lateinit var progressLoading: ProgressDialog
    var groupList = mutableListOf<Group>()
    var username = ""
    private var membersize = 0
    val db = Firebase.firestore
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
        val view = inflater.inflate(R.layout.fragment_created_group, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
        }

        // Find the ImageButton in the fragment_group.xml layout
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
                val temp = ArrayList<Group>() // filtered list

                // This will filter the task list
                for (task in groupList) {

                    // get the lowercase and uppercase
                    if (task.groupName.lowercase(Locale.getDefault()).contains(search) || task.groupName.uppercase(
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
                val temp = ArrayList<Group>() //  filter  list

                // This will filter the task list
                for (task in groupList){

                    // get the lowercase and uppercase
                    if (task.groupName.lowercase(Locale.getDefault()).contains(search) || task.groupName.uppercase(
                            Locale.getDefault()).contains(search)){
                        temp.add(task)
                    }
                }
                // Add your search logic here
                adapter.updateList(temp)
                return true
            }
        })

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setLayoutManager(LinearLayoutManager(requireContext()))
        groupList = ArrayList()
        adapter = CreatedGroupAdapter(groupList)
        recyclerView.adapter = adapter
        adapter.setOnCancelListener(::onCancel)
        loadGroup()

        val floatingActionButton = view.findViewById<ImageButton>(R.id.floatingActionButton2)
        floatingActionButton.setOnClickListener {
            val fragment = GroupFragment()
            val bundle = Bundle()
            bundle.putString("username", username)
            fragment.arguments = bundle
            replaceFragment(fragment)
        }





        return view
    }

    private fun replaceFragment(fragment:Fragment){
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // This method will load the group
    @SuppressLint("NotifyDataSetChanged")
    fun loadGroup(){

        // Get the username ID
        db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener {
            if (it.isEmpty) {
                Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
            }else{
                for (document in it){

                    // Get the UserID retrieve
                    // Then
                    val iD = document.data["UserID"].toString()

                    // Load the group collections
                    db.collection("Group").whereEqualTo("UserID",iD).get().addOnSuccessListener { groupDocuments ->
                        for (groupDocument in groupDocuments){
                            // Get the group name
                            val groupName = groupDocument.data["GroupName"].toString()

                            // Get the group description
                            val groupDescription = groupDocument.data["GroupDescription"].toString()

                            // Get the group members collection
                            val collectionReference = db.collection("GroupMembers")

                            // Query the collection to get the group ID
                            val query = collectionReference.whereEqualTo("GroupID", iD)

                            // Get the group members size
                            query.get().addOnSuccessListener { memberDocuments ->
                                membersize = memberDocuments.size() +  1

                                // Create a group object
                                val group = Group(groupName, groupDescription, membersize.toString())

                                // Add the group object in the grouplist.
                                groupList.add(group)

                                // Notify the adapter of the recycleviewer
                                adapter.notifyDataSetChanged()
                            }.addOnFailureListener {
                                Toast.makeText(context, "Error getting documents: ", Toast.LENGTH_SHORT).show()
                            }

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
         * @return A new instance of fragment CreatedGroup.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreatedGroup().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCancel(position: Int) {

        // Get the taskname from the list
        val groupName = groupList[position].groupName
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
            append(groupName)
            append("?")
        }

        val dialog = builder.setView(dialogLayout).create()

        dialog.show()

        button.setOnClickListener {

            // Load the progress dialog
            progressLoading = ProgressDialog(requireContext())
            progressLoading.setTitle("Deleting Group...")
            progressLoading.setMessage("Please wait...")
            progressLoading.setCanceledOnTouchOutside(false)
            progressLoading.show()

            // This will  find the group name on group collections
            db.collection("Group").whereEqualTo("GroupName",groupName).get().addOnSuccessListener { groupit->
                if (groupit.isEmpty){
                    Toast.makeText(context, "Group does not exist", Toast.LENGTH_SHORT).show()
                }else{

                    // Loadd the documents in the group collection
                    for  (groupdocs in groupit){

                        // Get the docs id and group id
                        val groupiddocs =  groupdocs.id
                        val groupidString = groupdocs.data["GroupID"].toString()
                        var groupid = 0

                        // This will check if the group id is not null or empty
                        if (groupidString != "null" && groupidString.isNotEmpty()) {
                            try {

                                // Convert the group id to integer
                                groupid = Integer.parseInt(groupidString)

                                // Find the group id on task collections
                                db.collection("Task").whereEqualTo("GroupID",groupidString).get().addOnSuccessListener { taskit ->
                                    if (taskit.isEmpty) {
                                        progressLoading.dismiss()

                                        //  This will delete the group collections, if the task does not exist
                                        db.collection("Group").document(groupiddocs).delete()
                                            .addOnSuccessListener {
                                                success()
                                                groupList.removeAt(position)
                                                adapter.notifyDataSetChanged()
                                            }
                                    } else {

                                        //  Load the task in the documents  and delete the task
                                        for (taskdocs in taskit) {


                                            // get the task id document
                                            val taskid = taskdocs.id

                                            db.collection("GroupMembers")
                                                .whereEqualTo("GroupID", groupid).get()
                                                .addOnSuccessListener { memberit ->
                                                    if (memberit.isEmpty) {
                                                        Toast.makeText(
                                                            context,
                                                            "GroupID member does not exist",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        //  This will delete the task and group collections, if the member does not exist
                                                        db.collection("Task").document(taskid)
                                                            .delete().addOnSuccessListener {
                                                                db.collection("Group")
                                                                    .document(groupiddocs).delete()
                                                                    .addOnSuccessListener {
                                                                        progressLoading.dismiss()
                                                                        success()
                                                                        groupList.removeAt(position)
                                                                        adapter.notifyDataSetChanged()
                                                                    }
                                                            }

                                                    } else {

                                                        // Load the member in the documents and delete the member
                                                        for (memberdocs in memberit) {

                                                            // Get the member document id
                                                            val memberid = memberdocs.id

                                                            // Delete the members, task, and group collections
                                                            db.collection("GroupMembers")
                                                                .document(memberid).delete()
                                                                .addOnSuccessListener {
                                                                    db.collection("Task")
                                                                        .document(taskid).delete()
                                                                        .addOnSuccessListener {
                                                                            db.collection("Group")
                                                                                .document(
                                                                                    groupiddocs
                                                                                ).delete()
                                                                                .addOnSuccessListener {
                                                                                    progressLoading.dismiss()
                                                                                    success()
                                                                                    groupList.removeAt(
                                                                                        position
                                                                                    )
                                                                                    adapter.notifyDataSetChanged()
                                                                                }
                                                                        }
                                                                }
                                                        }
                                                    }
                                                }
                                        }
                                    }


                                }
                            } catch (e: NumberFormatException) {
                                // handle the exception
                                Toast.makeText(context, "GroupID does not exist or null", Toast.LENGTH_SHORT).show()
                                progressLoading.dismiss()
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

    //  This method used for success
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
            append("Group Alert")
        }
        messageText1.text = buildString {
            append("Group Delete Successfully .")
        }

        val dialog1 = builder1.setView(dialogLayout1).create()

        dialog1.show()
        button1.setOnClickListener{
            dialog1.dismiss()
        }
    }

}