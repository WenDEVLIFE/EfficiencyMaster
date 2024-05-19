package com.example.efficiencymaster

import adapters.GroupTaskAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import classes.GroupTaskInfo
import classes.NonInterceptingLinearLayoutManager
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
    val db = Firebase.firestore

    var username = ""
    var groupNameIntent = ""
    var grouptaskList = mutableListOf<GroupTaskInfo>()

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
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_grouptask, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username  =  it.getString("username").toString()
            groupNameIntent  =  it.getString("groupName").toString()
        }

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
               /* val temp = ArrayList<Task>() // filtered list

                // This will filter the task list
                for (task in taskList) {

                    // get the lowercase and uppercase
                    if (task.taskname.lowercase(Locale.getDefault()).contains(search) || task.taskname.uppercase(
                            Locale.getDefault()).contains(search)) {
                        temp.add(task)
                    }
                }
                adapter.updateList(temp) */
                // Add your search logic here
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var search: String = newText?.lowercase(Locale.getDefault()) ?: return false
              /*  val temp = ArrayList<Task>() //  filter  list

                // This will filter the task list
                for (task in taskList){

                    // get the lowercase and uppercase
                    if (task.taskname.lowercase(Locale.getDefault()).contains(search) || task.taskname.uppercase(
                            Locale.getDefault()).contains(search)){
                        temp.add(task)
                    }
                }
                // Add your search logic here
                adapter.updateList(temp) */
                return true
            }
        })

        // This is for floating action button
        val fabMenu = view.findViewById<FloatingActionMenu>(R.id.fab_menu)
        val fabOption1 = view.findViewById<FloatingActionButton>(R.id.fab_option1)
        val fabOption2 = view.findViewById<FloatingActionButton>(R.id.fab_option2)
        val fabOption3 = view.findViewById<FloatingActionButton>(R.id.fab_option3)
        val fabOption4 = view.findViewById<FloatingActionButton>(R.id.fab_option4)

        fabOption1.setOnClickListener {
            // Handle option 1 click
            fabMenu.close(true)
            Toast.makeText(context, "Add Group Task", Toast.LENGTH_SHORT).show()
            fragment = CreateGroupTask()
            bundle = Bundle()
            bundle.putString("username", username)
            bundle.putString("groupName", groupNameIntent)
            fragment.arguments = bundle
            replaceFragment(fragment)

        }

        fabOption2.setOnClickListener {
            // Handle option 2 click
            fabMenu.close(true)
            Toast.makeText(context, "View Group Members", Toast.LENGTH_SHORT).show()
        }

        fabOption3.setOnClickListener {
            // Handle option 3 click
            fabMenu.close(true)
            Toast.makeText(context, "View Pending Task", Toast.LENGTH_SHORT).show()
        }

        fabOption4.setOnClickListener {
            // Handle option 4 click
            fabMenu.close(true)
            Toast.makeText(context, "View Pending Members", Toast.LENGTH_SHORT).show()
        }

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = NonInterceptingLinearLayoutManager(requireContext())
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

    private fun loadGroupTask(){
        db.collection("Group").whereEqualTo("groupName", groupNameIntent).get().addOnSuccessListener { grouptaskit ->
            if (grouptaskit.isEmpty) {
                Toast.makeText(context, "Group does not exist", Toast.LENGTH_SHORT).show()
            }else{
                for (groupdocument in grouptaskit){

                    val groupid =  groupdocument.data["GroupID"].toString()



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

    override fun onCancel(position: Int) {
        TODO("Not yet implemented")
    }
}