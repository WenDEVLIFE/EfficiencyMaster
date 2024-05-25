package com.example.efficiencymaster

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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PendingMembers.newInstance] factory method to
 * create an instance of this fragment.
 */
class PendingMembers : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerView:RecyclerView
    lateinit var progressLoading:ProgressDialog
    val db = Firebase.firestore


    var username =""
    var groupNameIntent=""
    private val networkManager = NetworkManager()

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
        val view  = inflater.inflate(R.layout.fragment_pending_members, container, false)

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
        /* searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        }) */


        val floatingActionButton = view.findViewById<ImageButton>(R.id.floatingActionButton2)
        floatingActionButton.setOnClickListener {
            val fragment = GroupTask()
            val bundle = Bundle()
            bundle.putString("username", username)
            bundle.putString("groupName", groupNameIntent)
            fragment.arguments = bundle
            replaceFragment(fragment)
        }

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setLayoutManager(LinearLayoutManager(context))


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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PendingMembers.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PendingMembers().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}