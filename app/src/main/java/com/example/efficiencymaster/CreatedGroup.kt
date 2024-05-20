package com.example.efficiencymaster

import adapters.CreatedGroupAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import classes.Group
import classes.GroupTaskInfo
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
    var groupList = mutableListOf<Group>()
    var username = ""
    private var membersize = 0
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
                var search: String = query?.lowercase(Locale.getDefault()) ?: return false
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
                var search: String = newText?.lowercase(Locale.getDefault()) ?: return false
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





        return view
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

    override fun onCancel(position: Int) {
        TODO("Not yet implemented")
    }
}