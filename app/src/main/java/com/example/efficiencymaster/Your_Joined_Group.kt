package com.example.efficiencymaster

import adapters.GroupAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
 * Use the [Your_Joined_Group.newInstance] factory method to
 * create an instance of this fragment.
 */
class Your_Joined_Group : Fragment(), GroupAdapter.OnCancelListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recycleviewer:RecyclerView
    lateinit var adapter: GroupAdapter
    val db = Firebase.firestore
    var groupList = mutableListOf<Group>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_your__joined__group, container, false)

        // Find the ImageButton in the fragment_group.xml layout
        val ImageButton = view.findViewById<ImageButton>(R.id.imageButton)
        ImageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.OpenDrawer()

        }

        // This is for search functions
        val searchView = view.findViewById<SearchView>(R.id.search_group)
        /* searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                // This is for searching  the group
                var search: String = query?.lowercase(Locale.getDefault()) ?: return false
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
                var search: String = newText?.lowercase(Locale.getDefault()) ?: return false
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
        })  */
        // This will get the recycler view from the fragment_group.xml layout
        // and set the layout manager to linear layout manager and set the adapter
        // to the task adapter
        recycleviewer = view.findViewById(R.id.recycler_view)
        recycleviewer.setLayoutManager(LinearLayoutManager(context))
        groupList = ArrayList()
        adapter = GroupAdapter(groupList)
        recycleviewer.adapter = adapter
        adapter.setOnCancelListener(::onCancel)



            return view;
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
            Your_Joined_Group().apply {
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