package com.example.efficiencymaster

import adapters.GroupAdapter
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
 * Use the [GroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupFragment : Fragment(), GroupAdapter.OnCancelListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerView: RecyclerView
    lateinit var bundle:Bundle
    lateinit var fragment:Fragment
    lateinit var adapter:GroupAdapter
    val db = Firebase.firestore
    var groupList = mutableListOf<Group>()
    var username = ""

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
        val view = inflater.inflate(R.layout.fragment_group, container, false)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            username = it.getString("username").toString()
        }

        // Find the ImageButton in the fragment_group.xml layout
        val ImageButton = view.findViewById<ImageButton>(R.id.imageButton)
        ImageButton.setOnClickListener {

            // Open the drawer when the ImageButton is clicked
            val activity = activity as MainActivity
            activity.OpenDrawer()

        }

        // This is for search functions
        val searchView = view.findViewById<SearchView>(R.id.search_group)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                var search: String = query?.lowercase(Locale.getDefault()) ?: return false
                // Add your search logic here
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var search: String = newText?.lowercase(Locale.getDefault()) ?: return false
                // Add your search logic here
                return true
            }
        })
        // This is for floating action button
        val fabMenu = view.findViewById<FloatingActionMenu>(R.id.fab_menu)
        val fabOption1 = view.findViewById<FloatingActionButton>(R.id.fab_option1)
        val fabOption2 = view.findViewById<FloatingActionButton>(R.id.fab_option2)
        val fabOption3 = view.findViewById<FloatingActionButton>(R.id.fab_option3)

        fabOption1.setOnClickListener {
            // Handle option 1 click
            fabMenu.close(true)
            fragment = CreateGroupTask()
            bundle = Bundle()
            bundle.putString("username", username)
            fragment.arguments = bundle
            ReplaceFragment(fragment)
        }

        fabOption2.setOnClickListener {
            // Handle option 2 click
            fabMenu.close(true)
            Toast.makeText(context, "View Group Members", Toast.LENGTH_SHORT).show()
        }

        fabOption3.setOnClickListener {
            // Handle option 3 click
            fabMenu.close(true)
            Toast.makeText(context, "Your Created Groups", Toast.LENGTH_SHORT).show()
        }

       // Get the recycleviwer id, set layout, call the group list and set to array
        // also set the recycleviewer to its adapter and call the Load Group Method
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        groupList = ArrayList()
        adapter = GroupAdapter(groupList)
        recyclerView.adapter = adapter
        adapter.setOnCancelListener(::onCancel)
        LoadGroup()

        return view
    }

      // Method used to Replae the fragment
    fun ReplaceFragment(fragment:Fragment){
        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    // This method will load the group
    fun LoadGroup(){

        // Get the username ID
        db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener {
            if (it.isEmpty) {
                Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
            }else{
                for (document in it){

                    // Get the UserID retrieve

                    // Then
                    val ID = document.data["UserID"].toString()
                    db.collection("Group").get().addOnSuccessListener { groupDocuments ->
                        for (groupDocument in groupDocuments){
                            // Get the group name
                            val groupName = groupDocument.data["GroupName"].toString()

                            // Get the group description
                            val groupDescription = groupDocument.data["GroupDescription"].toString()

                            // Get the group members collection
                            val collectionReference = db.collection("GroupMembers")

                            // Query the collection to get the group ID
                            val query = collectionReference.whereEqualTo("GroupID", ID)

                            // member size variable
                            var membersize= 0

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
         * @return A new instance of fragment GroupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCancel(position: Int) {

        val GroupName = groupList[position].groupName

        // Customize alert dialog below
        val builder = android.app.AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.message_layout2, null)

        val titleText = dialogLayout.findViewById<TextView>(R.id.dialog_title)
        val messageText = dialogLayout.findViewById<TextView>(R.id.dialog_message)
        val button = dialogLayout.findViewById<Button>(R.id.dialog_button)
        val button2 = dialogLayout.findViewById<Button>(R.id.dialog_button2)
        val ImageView1 = dialogLayout.findViewById<ImageView>(R.id.imageView2)

        // Load the gif image
        Glide.with(requireContext())
            .asGif()
            .load(R.drawable.confused)
            .into(ImageView1)
        ImageView1.scaleType = ImageView.ScaleType.FIT_CENTER
        val params = ImageView1.layoutParams

        // Convert dp to pixels
        val scale = resources.displayMetrics.density
        params.width = (100 * scale).toInt()  // 100dp in pixels
        params.height = (100 * scale).toInt() // 100dp in pixels

        ImageView1.layoutParams = params
        titleText.text = "Delete the Task "
        messageText.text = "Are you sure you want to join the group $GroupName?"

        val dialog = builder.setView(dialogLayout).create() // Create AlertDialog instance

        button.setOnClickListener{

            dialog.dismiss()
        }

        button2.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}