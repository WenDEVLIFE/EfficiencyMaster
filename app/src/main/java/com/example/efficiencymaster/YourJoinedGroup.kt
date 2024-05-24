package com.example.efficiencymaster

import adapters.JoinedGroupAdapter
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [YourJoinedGroup.newInstance] factory method to
 * create an instance of this fragment.
 */
class YourJoinedGroup : Fragment(), JoinedGroupAdapter.OnCancelListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recycleviewer:RecyclerView
    lateinit var adapter:JoinedGroupAdapter
    val db = Firebase.firestore
    var groupList = mutableListOf<Group>()
    var username = ""
    var membersize = 0
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
        val view =  inflater.inflate(R.layout.fragment_your__joined__group, container, false)

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

                // This is for searching  the group
                val search: String = query?.lowercase(Locale.getDefault()) ?: return false
                val temp = ArrayList<Group>()

                // loop the group list and filter the value
                for (group in groupList){
                    if (group.groupName.lowercase(Locale.getDefault()).contains(search) || group.groupName.uppercase(Locale.getDefault()).contains(search)){
                        temp.add(group)
                    }
                }
                adapter.updateList(temp)
                // Add your search logic here
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                // This is for searching the group
                val search: String = newText?.lowercase(Locale.getDefault()) ?: return false
                val temp = ArrayList<Group>()

                //  loop the grouplist and filter the value
                for (group in groupList){
                    if (group.groupName.lowercase(Locale.getDefault()).contains(search) || group.groupName.uppercase(Locale.getDefault()).contains(search)){
                        temp.add(group)
                    }
                }
                adapter.updateList(temp)
                // Add your search logic here
                return true
            }
        })


        // This will get the recycler view from the fragment_group.xml layout
        // and set the layout manager to linear layout manager and set the adapter
        // to the task adapter
        recycleviewer = view.findViewById(R.id.recycler_view)
        recycleviewer.setLayoutManager(LinearLayoutManager(context))
        groupList = ArrayList()
        adapter = JoinedGroupAdapter(groupList)
        recycleviewer.adapter = adapter
        adapter.setOnCancelListener(::onCancel)
        loadJoinedGroup()

        val floatingActionButton = view.findViewById<ImageButton>(R.id.floatingActionButton2)
        floatingActionButton.setOnClickListener {
            val fragment = GroupFragment()
            val Bundle = Bundle()
            Bundle.putString("username", username)
            fragment.arguments = Bundle
            replaceFragment(fragment)
        }



            return view
    }

    // This will load the join user groups
    @SuppressLint("NotifyDataSetChanged")
    private fun loadJoinedGroup() {

        // Find the username
        db.collection("User").whereEqualTo("username",username).get()
            .addOnSuccessListener { userIt ->
                if (userIt.isEmpty){
                     Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
               }else{

                   // Load the retrieve username id
                   for (document in userIt){
                       val userID = document.data["UserID"].toString()

                       // Find the username id on the group members
                       db.collection("GroupMembers").whereEqualTo("UserID",userID).get()
                           .addOnSuccessListener{ memberIT ->
                               if(memberIT.isEmpty){
                                      Toast.makeText(context, "Group Member does not exist", Toast.LENGTH_SHORT).show()
                               }else{

                                   // Retrieve the group member id
                                   for (groupdocumentmembers in memberIT){
                                       val groupID = groupdocumentmembers.data["GroupID"].toString()

                                        // Load the group
                                       db.collection("Group").get()
                                           .addOnSuccessListener { groupIt ->
                                              if(groupIt.isEmpty){
                                                    Toast.makeText(context, "Group does not exist", Toast.LENGTH_SHORT).show()
                                              }
                                               else{

                                                   // Retrive the group name, description and id
                                                  for (groupDocument in groupIt){
                                                      val groupName = groupDocument.data["GroupName"].toString()
                                                      val groupDescription = groupDocument.data["GroupDescription"].toString()
                                                      val groupIds = groupDocument.data["GroupID"].toString()

                                                      // This will check if the group id member is equal to the group id of group.
                                                    if (groupID == groupIds){
                                                        // Get the group members collection
                                                        val collectionReference = db.collection("GroupMembers")

                                                        // Query the collection to get the group ID
                                                        val query = collectionReference.whereEqualTo("GroupID", groupID)


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
                                           }.addOnFailureListener {
                                               Toast.makeText(context, "Error getting documents: ", Toast.LENGTH_SHORT).show()
                                           }
                                   }
                               }
                           }.addOnFailureListener {
                               Toast.makeText(context, "Error getting documents: ", Toast.LENGTH_SHORT).show()
                           }
                   }
               }
            }.addOnFailureListener{
                Toast.makeText(context, "Error getting documents: ", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Your_Joined_Group.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            YourJoinedGroup().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCancel(position: Int) {
        val groupName = groupList[position].groupName
        val fragment = GroupTask()
        val Bundle = Bundle()
        Bundle.putString("username", username)
        Bundle.putString("groupName", groupName)
        fragment.arguments = Bundle
        replaceFragment(fragment)
        Toast.makeText(context, "Group Task $groupName", Toast.LENGTH_SHORT).show()


    }

    // Method used to Replae the fragment
    private fun replaceFragment(fragment:Fragment){
        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }
}