package com.example.efficiencymaster

import adapters.MemberAdapter
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import classes.GroupTaskInfo
import classes.Member
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Members.newInstance] factory method to
 * create an instance of this fragment.
 */
class Members : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var progressLoading: ProgressDialog
    private lateinit var recycleViewer:RecyclerView
    private lateinit var adapters: MemberAdapter
    val db = Firebase.firestore
    private val networkManager = NetworkManager()

    var username = ""
    private var groupNameIntent = ""
    var memberList = mutableListOf<Member>()
    var groupid = 0


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

        // This will check if user is connected to the internet.
        networkManager.checkNetworkAndExitIfNotAvailable(requireContext())

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_members, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
            groupNameIntent = it.getString("groupName").toString()
        }

        val imageButton = view.findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.openDrawer()

        }


        // This is for search functions
        val searchView = view.findViewById<SearchView>(R.id.search_group)
        /*searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                var search: String = query?.lowercase(Locale.getDefault()) ?: return false
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
                var search: String = newText?.lowercase(Locale.getDefault()) ?: return false
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
        }) */

        // Set  the recycleviewer and it's components.
        recycleViewer = view.findViewById(R.id.recycler_view)
        recycleViewer.setLayoutManager(LinearLayoutManager(requireContext()))
        memberList =  ArrayList()
        adapters = MemberAdapter(memberList)
        recycleViewer.adapter = adapters
        loadMembers()





        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMembers(){
        db.collection("Group").whereEqualTo("GroupName",groupNameIntent).get().addOnSuccessListener { groupit ->
            for (groupdocument in groupit){
                val groupIDsubString = groupdocument.data["GroupID"].toString()
                groupid = Integer.parseInt(groupIDsubString)

                db.collection("GroupMembers").whereEqualTo("GroupID", groupid).get().addOnSuccessListener{ membersit ->
                    for (memberdocument in membersit){
                        val memberID = memberdocument.data["UserID"].toString()
                        val joinedDate = memberdocument.data["Joined Date"].toString()
                        val role = memberdocument.data["Role"].toString()

                        db.collection("User").whereEqualTo("UserID", memberID).get().addOnSuccessListener { userit ->
                            for (userdocument in userit){
                                val username = userdocument.data["username"].toString()
                                val member = Member(username, role, memberID, joinedDate)
                                memberList.add(member)
                            }
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
         * @return A new instance of fragment Members.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Members().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}