package com.example.efficiencymaster

import adapters.GroupAdapter
import android.annotation.SuppressLint
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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import classes.Group
import com.bumptech.glide.Glide
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
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

    private lateinit var recyclerView: RecyclerView
    lateinit var bundle:Bundle
    lateinit var fragment:Fragment
    lateinit var adapter:GroupAdapter
    val db = Firebase.firestore
    var groupList = mutableListOf<Group>()
    var username = ""
    private var membersize = 0
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Replace with your previous fragment here
                val fragment = HomeFragmentation()
                val bundle = Bundle()
                bundle.putString("username", username)
                fragment.arguments = bundle
                replaceFragment((fragment))
            }
        })
        // This will check if user is connected to the internet.
        networkManager.checkNetworkAndExitIfNotAvailable(requireContext())

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_group, container, false)

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
        // This is for floating action button
        val fabMenu = view.findViewById<FloatingActionMenu>(R.id.fab_menu)
        val fabOption1 = view.findViewById<FloatingActionButton>(R.id.fab_option1)
        val fabOption2 = view.findViewById<FloatingActionButton>(R.id.fab_option2)
        val fabOption3 = view.findViewById<FloatingActionButton>(R.id.fab_option3)

        fabOption1.setOnClickListener {
            // Handle option 1 click
            // This will joined to CreateGroup
            fabMenu.close(true)
            fragment = CreateGroup()
            bundle = Bundle()
            bundle.putString("username", username)
            fragment.arguments = bundle
            replaceFragment(fragment)
        }

        fabOption2.setOnClickListener {
            // Handle option 2 click
            // This will go to the joined groups
            fabMenu.close(true)
            fragment = YourJoinedGroup()
            bundle = Bundle()
            bundle.putString("username",username)
            fragment.arguments = bundle
            replaceFragment(fragment)
            Toast.makeText(context, "View Joined Group", Toast.LENGTH_SHORT).show()
        }

        fabOption3.setOnClickListener {
            // Handle option 3 click
            fabMenu.close(true)
            //  This will go to created groups of user
            fragment = CreatedGroup()
            bundle  = Bundle()
            bundle.putString("username", username)
            fragment.arguments = bundle
            replaceFragment(fragment)
            Toast.makeText(context, "Your Created Groups", Toast.LENGTH_SHORT).show()
        }

        // This will get the recycler view from the fragment_group.xml layout
        // and set the layout manager to linear layout manager and set the adapter
        // to the task adapter
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        groupList = ArrayList()
        adapter = GroupAdapter(groupList)
        recyclerView.adapter = adapter
        adapter.setOnCancelListener(::onCancel)
        loadGroup()

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
                    db.collection("Group").get().addOnSuccessListener { groupDocuments ->
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

    // This is used to to join the group request
    override fun onCancel(position: Int) {
        val groupName = groupList[position].groupName
        val builder = android.app.AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.message_layout2, null)
        val titleText = dialogLayout.findViewById<TextView>(R.id.dialog_title)
        val messageText = dialogLayout.findViewById<TextView>(R.id.dialog_message)
        val button = dialogLayout.findViewById<Button>(R.id.dialog_button)
        button.text = buildString {
            append("Join the group")
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
        append("Join the group ")
    }
        messageText.text = buildString {
        append("Are you sure you want to join the group ")
        append(groupName)
        append("?")
    }

        val dialog = builder.setView(dialogLayout).create()

        dialog.show()

        button.setOnClickListener{
            db.collection("User").whereEqualTo("username",username).get().addOnSuccessListener { userit ->
                if (userit.isEmpty) {
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                }
                else {
                    val userdocument = userit.documents.first()
                    val userID = userdocument.data?.get("UserID").toString()
                    db.collection("Group").whereEqualTo("GroupName", groupName).get().addOnSuccessListener { groupits1  ->

                            val document = groupits1.documents.first()
                            val groupID = document.data?.get("GroupID").toString()
                            val groupids = Integer.parseInt(groupID)

                            // It will check if the user is already a member of the group
                            db.collection("GroupMembers").whereEqualTo("GroupID",groupids).get().addOnSuccessListener {  groupit ->
                                for (documennt in groupit){

                                        // hashmap for creating a request for the user
                                        val localDate = LocalDate.now()
                                        val groupMember = hashMapOf(
                                            "GroupID" to groupID,
                                            "UserID" to userID,
                                            "Role" to "Member",
                                            "Status" to "Pending",
                                            "Date Requested" to localDate.toString()
                                        )


                                        // Insert the pending request for joining the group
                                        db.collection("GroupMembers").whereEqualTo("GroupID",groupids).whereEqualTo("UserID", userID).get().addOnSuccessListener {groupits2  ->

                                            // This will check if the user is already a member of the group
                                            if (groupits2.isEmpty){

                                                // This  will check if the user already send a  request to join the group
                                               db.collection("PendingMembers").whereEqualTo("GroupID",groupids).whereEqualTo("UserID", userID).get().addOnSuccessListener {
                                                    if (it.isEmpty){

                                                        // Insert the pending request for joining the group
                                                        db.collection("PendingGroupMembers").add(groupMember)
                                                            .addOnSuccessListener {

                                                                // Load the success dialog
                                                                dialog.dismiss()
                                                                success()

                                                            }
                                                            .addOnFailureListener {
                                                                Toast.makeText(context, "Error sending request", Toast.LENGTH_SHORT).show()
                                                                dialog.dismiss()
                                                                warning()
                                                            }
                                                    }else{
                                                        warning()
                                                    }
                                                }
                                                dialog.dismiss()
                                            }else{

                                                // Load the warning dialog
                                                dialog.dismiss()
                                              Toast.makeText(context, "You already a member of the group", Toast.LENGTH_SHORT).show()
                                            }
                                        }



                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, "Error getting documents: ", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        button2.setOnClickListener {
            dialog.dismiss()
        }

    }


    // This method used to pop up sucess functions
    private fun success(){

        // below are the alert dialog components and etc.
        val builder1 = android.app.AlertDialog.Builder(context)
        val inflater1 = layoutInflater
        val dialogLayout1 = inflater1.inflate(R.layout.message_layout, null)
        val titleText1= dialogLayout1.findViewById<TextView>(R.id.dialog_title)
        val messageText1 = dialogLayout1.findViewById<TextView>(R.id.dialog_message)
        val button1 = dialogLayout1.findViewById<Button>(R.id.dialog_button)
        button1.text = getString(R.string.ok)
        val imageView2 = dialogLayout1.findViewById<ImageView>(R.id.imageView2)

        Glide.with(requireContext())
            .asGif()
            .load(R.drawable.paper_plane)
            .into(imageView2)
        imageView2.scaleType = ImageView.ScaleType.FIT_CENTER
        val params1 = imageView2.layoutParams
        val scale1 = resources.displayMetrics.density
        params1.width = (100 * scale1).toInt()
        params1.height = (100 * scale1).toInt()
        imageView2.layoutParams = params1
        titleText1.text = buildString {
        append("Request Send ")
    }
        messageText1.text = buildString {
        append("Request sent successfully. Please wait for the group admin to approve your request.")
    }

        val dialog1 = builder1.setView(dialogLayout1).create()

        dialog1.show()
        button1.setOnClickListener{
            dialog1.dismiss()
        }
    }


    // This method is used to pop up warning dialog
    private  fun warning(){

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
        append("Request Already Send ")
    }
        messageText1.text = buildString {
        append("Request already sent , Please wait for the approval.")
    }

        val dialog1 = builder1.setView(dialogLayout1).create()

        dialog1.show()
        button1.setOnClickListener{
            dialog1.dismiss()
        }
    }
}