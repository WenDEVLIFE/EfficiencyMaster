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
import androidx.appcompat.app.AlertDialog
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
        })
        // This is for floating action button
        val fabMenu = view.findViewById<FloatingActionMenu>(R.id.fab_menu)
        val fabOption1 = view.findViewById<FloatingActionButton>(R.id.fab_option1)
        val fabOption2 = view.findViewById<FloatingActionButton>(R.id.fab_option2)
        val fabOption3 = view.findViewById<FloatingActionButton>(R.id.fab_option3)

        fabOption1.setOnClickListener {
            // Handle option 1 click
            // This will joined to CreateGroupTask
            fabMenu.close(true)
            fragment = CreateGroupTask()
            bundle = Bundle()
            bundle.putString("username", username)
            fragment.arguments = bundle
            ReplaceFragment(fragment)
        }

        fabOption2.setOnClickListener {
            // Handle option 2 click
            // This will go to the joined groups
            fabMenu.close(true)
            fragment = Your_Joined_Group()
            bundle = Bundle()
            bundle.putString("username",username)
            fragment.arguments = bundle
            ReplaceFragment(fragment)
            Toast.makeText(context, "View Joined Group", Toast.LENGTH_SHORT).show()
        }

        fabOption3.setOnClickListener {
            // Handle option 3 click
            fabMenu.close(true)
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
        val builder = android.app.AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.message_layout2, null)
        val titleText = dialogLayout.findViewById<TextView>(R.id.dialog_title)
        val messageText = dialogLayout.findViewById<TextView>(R.id.dialog_message)
        val button = dialogLayout.findViewById<Button>(R.id.dialog_button)
        button.setText("Join the group")
        val button2 = dialogLayout.findViewById<Button>(R.id.dialog_button2)
        val ImageView1 = dialogLayout.findViewById<ImageView>(R.id.imageView2)

        Glide.with(requireContext())
            .asGif()
            .load(R.drawable.confused)
            .into(ImageView1)
        ImageView1.scaleType = ImageView.ScaleType.FIT_CENTER
        val params = ImageView1.layoutParams
        val scale = resources.displayMetrics.density
        params.width = (100 * scale).toInt()
        params.height = (100 * scale).toInt()
        ImageView1.layoutParams = params
        titleText.text = "Join the group "
        messageText.text = "Are you sure you want to join the group $GroupName?"

        val dialog = builder.setView(dialogLayout).create()

        dialog.show()

        button.setOnClickListener{
            db.collection("User").whereEqualTo("username",username).get().addOnSuccessListener {
                for (document in it){
                    val UserID = document.data["UserID"].toString()
                    db.collection("Group").whereEqualTo("GroupName", GroupName).get().addOnSuccessListener {
                        for (document in it){

                            val GroupID = document.data["GroupID"].toString()

                            // It will check if the user is already a member of the group
                            db.collection("GroupMembers").get().addOnSuccessListener {
                                for (documennt in it){

                                    val GroupID_mem = documennt.data["GroupID"].toString()
                                    val GroupUserID = documennt.data["UserID"].toString()

                                    // if the user is already a member then it wont add or send a request
                                    if (GroupID == GroupID_mem && UserID == GroupUserID) {
                                        Toast.makeText(
                                            context,
                                            "You are already a member of this group",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        dialog.dismiss()
                                    }else{

                                        // hashmap for creating a request for the user
                                        val LocalDate = LocalDate.now()
                                        val groupMember = hashMapOf(
                                            "GroupID" to GroupID,
                                            "UserID" to UserID,
                                            "Role" to "Member",
                                            "Status" to "Pending",
                                            "Date Requested" to LocalDate.toString()
                                        )


                                        // Insert the pending request for joining the group
                                        db.collection("PendingGroupMembers").whereEqualTo("GroupID",GroupID).whereEqualTo("UserID", UserID).get().addOnSuccessListener {
                                            if (it.isEmpty()){
                                                db.collection("PendingGroupMembers").add(groupMember)
                                                    .addOnSuccessListener {

                                                        // Load the success dialog
                                                        dialog.dismiss()
                                                       Success()

                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(context, "Error sending request", Toast.LENGTH_SHORT).show()
                                                        dialog.dismiss()
                                                    }
                                            }else{

                                                // Load the warning dialog
                                                dialog.dismiss()
                                                Warning()
                                            }
                                        }
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
    fun Success(){

        // below are the alert dialog components and etc.
        val builder1 = android.app.AlertDialog.Builder(context)
        val inflater1 = layoutInflater
        val dialogLayout1 = inflater1.inflate(R.layout.message_layout, null)
        val titleText1= dialogLayout1.findViewById<TextView>(R.id.dialog_title)
        val messageText1 = dialogLayout1.findViewById<TextView>(R.id.dialog_message)
        val button1 = dialogLayout1.findViewById<Button>(R.id.dialog_button)
        button1.setText("Ok")
        val ImageView2 = dialogLayout1.findViewById<ImageView>(R.id.imageView2)

        Glide.with(requireContext())
            .asGif()
            .load(R.drawable.paper_plane)
            .into(ImageView2)
        ImageView2.scaleType = ImageView.ScaleType.FIT_CENTER
        val params1 = ImageView2.layoutParams
        val scale1 = resources.displayMetrics.density
        params1.width = (100 * scale1).toInt()
        params1.height = (100 * scale1).toInt()
        ImageView2.layoutParams = params1
        titleText1.text = "Request Send "
        messageText1.text = "Request sent successfully. Please wait for the group admin to approve your request."

        val dialog1 = builder1.setView(dialogLayout1).create()

        dialog1.show()
        button1.setOnClickListener{
            dialog1.dismiss()
        }
    }


    // This method is used to pop up warning dialog
    fun Warning(){

        // below are the customize alert dialgo components and etc.
        val builder1 = android.app.AlertDialog.Builder(context)
        val inflater1 = layoutInflater
        val dialogLayout1 = inflater1.inflate(R.layout.message_layout, null)
        val titleText1= dialogLayout1.findViewById<TextView>(R.id.dialog_title)
        val messageText1 = dialogLayout1.findViewById<TextView>(R.id.dialog_message)
        val button1 = dialogLayout1.findViewById<Button>(R.id.dialog_button)
        button1.setText("Ok")
        val ImageView2 = dialogLayout1.findViewById<ImageView>(R.id.imageView2)

        Glide.with(requireContext())
            .asGif()
            .load(R.drawable.alert)
            .into(ImageView2)
        ImageView2.scaleType = ImageView.ScaleType.FIT_CENTER
        val params1 = ImageView2.layoutParams
        val scale1 = resources.displayMetrics.density
        params1.width = (100 * scale1).toInt()
        params1.height = (100 * scale1).toInt()
        ImageView2.layoutParams = params1
        titleText1.text = "Request Already Send "
        messageText1.text = "Request already sent , Please wait for the approval."

        val dialog1 = builder1.setView(dialogLayout1).create()

        dialog1.show()
        button1.setOnClickListener{
            dialog1.dismiss()
        }
    }
}