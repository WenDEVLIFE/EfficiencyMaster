package com.example.efficiencymaster

import adapters.MemberAdapter
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
import classes.GroupTaskInfo
import classes.Member
import com.bumptech.glide.Glide
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
 * Use the [Members.newInstance] factory method to
 * create an instance of this fragment.
 */
class Members : Fragment(), MemberAdapter.OnDeleteListener, MemberAdapter.OnEditListener, MemberAdapter.OnCancelListener {
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
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val search: String = query?.lowercase(Locale.getDefault()) ?: return false
                val temp = ArrayList<Member>() // filtered list

                // This will filter the task list
                for (user in memberList) {

                    // get the lowercase and uppercase
                    if (user.username.lowercase(Locale.getDefault()).contains(search) || user.username.uppercase(
                            Locale.getDefault()).contains(search)) {
                        temp.add(user)
                    }
                }
                adapters.updateList(temp)
                // Add your search logic here
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val search: String = newText?.lowercase(Locale.getDefault()) ?: return false
                val temp = ArrayList<Member>() //  filter  list

                // This will filter the task list
                for (user in memberList){

                    // get the lowercase and uppercase
                    if (user.username.lowercase(Locale.getDefault()).contains(search) || user.username.uppercase(
                            Locale.getDefault()).contains(search)){
                        temp.add(user)
                    }
                }
                // Add your search logic here
                adapters.updateList(temp)
                return true
            }
        })

        // Set  the recycleviewer and it's components.
        recycleViewer = view.findViewById(R.id.recycler_view)
        recycleViewer.setLayoutManager(LinearLayoutManager(requireContext()))
        memberList =  ArrayList()
        adapters = MemberAdapter(memberList)
        recycleViewer.adapter = adapters
        adapters.setOnCancelListener(::onCancel)
        adapters.setOnDeleteListener(::onDelete)
        adapters.setOnEditListener(::onEdit)
        loadMembers()

        // This is a back button
        val floatingActionButton = view.findViewById<ImageButton>(R.id.floatingActionButton2)
        floatingActionButton.setOnClickListener {
            val fragment = GroupTask()
            val bundle = Bundle()
            bundle.putString("username", username)
            bundle.putString("groupName", groupNameIntent)
            fragment.arguments = bundle
            replaceFragment(fragment)

        }





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

    @SuppressLint("NotifyDataSetChanged")

    // This method is used to load the members of the group
    private fun loadMembers(){

        // Find the group name from the group collecitons
        db.collection("Group").whereEqualTo("GroupName",groupNameIntent).get().addOnSuccessListener { groupit ->
            for (groupdocument in groupit){

                // get the group id and convert it to string
                val groupIDsubString = groupdocument.data["GroupID"].toString()

                // convert the string to integer
                groupid = Integer.parseInt(groupIDsubString)

                //Find the group members where equal to group id
                db.collection("GroupMembers").whereEqualTo("GroupID", groupid).get().addOnSuccessListener{ membersit ->
                    for (memberdocument in membersit){

                        // Load the user id, joined date and role
                        val memberID = memberdocument.data["UserID"].toString()
                        val joinedDate = memberdocument.data["Joined Date"].toString()
                        val role = memberdocument.data["Role"].toString()

                        // Find the user id from the user collection
                        db.collection("User").whereEqualTo("UserID", memberID).get().addOnSuccessListener { userit ->
                            for (userdocument in userit){

                                // Load the username from the user collection  and add it on the list
                                val username = userdocument.data["username"].toString()
                                val member = Member(username, role, memberID, joinedDate)
                                memberList.add(member) // Add the member to the list
                            }

                            // Notify the adapter
                            adapters.notifyDataSetChanged()
                        }
                    }


                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Error loading members", Toast.LENGTH_SHORT).show()
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

    // This remove the member from the group
    override fun onDelete(position: Int) {

        // get the username position on the list
        val usernameVal = memberList[position].username

        // Custom alert dialog below
        val builder = android.app.AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.message_layout2, null)
        val titleText = dialogLayout.findViewById<TextView>(R.id.dialog_title)
        val messageText = dialogLayout.findViewById<TextView>(R.id.dialog_message)
        val button = dialogLayout.findViewById<Button>(R.id.dialog_button)
        button.text = buildString {
            append("Remove")
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
            append("Remove user from group")
        }
        messageText.text = buildString {
            append("Are you sure you remove this member from the group")
            append(usernameVal)
            append("?")
        }

        val dialog = builder.setView(dialogLayout).create()

        dialog.show()

        button.setOnClickListener{

            // Load the progress dialog
            progressLoading = ProgressDialog(requireContext())
            progressLoading.setTitle("Loading")
            progressLoading.setMessage("Wait while loading...")
            progressLoading.setCanceledOnTouchOutside(false)
            progressLoading.show()

            // check if username is the same as the current user
            if(usernameVal == username){
                Toast.makeText(requireContext(), "You cannot remove yourself from the group", Toast.LENGTH_SHORT).show()
                progressLoading.dismiss()
            }else{

                // Find the group name from the group collecitons
                db.collection("Group").whereEqualTo("GroupName", groupNameIntent).get().addOnSuccessListener { groupit ->
                    for (groupdocument in groupit) {

                        // then get the group id then convert it to string
                        val groupIDsubString = groupdocument.data["GroupID"].toString()
                        groupid = Integer.parseInt(groupIDsubString)

                        // Find the group members where equal to group id
                        db.collection("GroupMembers").whereEqualTo("GroupID", groupid).get()
                            .addOnSuccessListener { membersit ->
                                for (memberdocument in membersit) {

                                    // load the user id and get it
                                    val memberID = memberdocument.data["UserID"].toString()

                                    // This will check if member id is equal to retrieve id on the collection
                                    if (memberID == memberList[position].userid) {

                                        // Then remove the group members from the group
                                        db.collection("GroupMembers").document(memberdocument.id)
                                            .delete().addOnSuccessListener {
                                                memberList.removeAt(position)
                                                success()
                                                adapters.notifyDataSetChanged()
                                                dialog.dismiss()
                                            }
                                    }
                                }

                            }.addOnFailureListener {
                                Toast.makeText(requireContext(), "Error removing member", Toast.LENGTH_SHORT).show()
                                progressLoading.dismiss()
                            }
                    }
                }.addOnSuccessListener {
                    Toast.makeText(requireContext(), "Error removing member", Toast.LENGTH_SHORT).show()
                    progressLoading.dismiss()
                }
            }

        }

        button2.setOnClickListener {
            dialog.dismiss()
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onCancel(position: Int) {
// get the username position on the list
        val usernameVal = memberList[position].username

        // Custom alert dialog below
        val builder = android.app.AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.message_layout2, null)
        val titleText = dialogLayout.findViewById<TextView>(R.id.dialog_title)
        val messageText = dialogLayout.findViewById<TextView>(R.id.dialog_message)
        val button = dialogLayout.findViewById<Button>(R.id.dialog_button)
        button.text = buildString {
            append("Remove as moderator")
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
            append("Remove to Moderator")
        }
        messageText.text = buildString {
            append("Are you sure you want to remove this user as a moderator")
            append(usernameVal)
            append("?")
        }

        val dialog = builder.setView(dialogLayout).create()

        dialog.show()

        button.setOnClickListener{

            // Load the progress dialog
            progressLoading = ProgressDialog(requireContext())
            progressLoading.setTitle("Loading")
            progressLoading.setMessage("Wait while loading...")
            progressLoading.setCanceledOnTouchOutside(false)
            progressLoading.show()

            // check if username is the same as the current user
            if(usernameVal == username){
                Toast.makeText(requireContext(), "You cannot remove yourself as a moderator", Toast.LENGTH_SHORT).show()
                progressLoading.dismiss()
            }else{

                // Find the group name from the group collecitons
                db.collection("Group").whereEqualTo("GroupName", groupNameIntent).get().addOnSuccessListener { groupit ->
                    for (groupdocument in groupit) {

                        // then get the group id then convert it to string
                        val groupIDsubString = groupdocument.data["GroupID"].toString()
                        groupid = Integer.parseInt(groupIDsubString)

                        // Find the group members where equal to group id
                        db.collection("GroupMembers").whereEqualTo("GroupID", groupid).get()
                            .addOnSuccessListener { membersit ->
                                for (memberdocument in membersit) {

                                    // load the user id and get it
                                    val memberID = memberdocument.data["UserID"].toString()
                                    val role = memberdocument.data["Role"].toString()

                                    // This will check if member id is equal to retrieve id on the collection
                                    if (memberID == memberList[position].userid) {

                                        // This will check if the user is not a group admin or group moderator, if not it will change the role
                                        if (role!= "Group_Admin"){

                                            // update the role of the user
                                            db.collection("GroupMembers").document(memberdocument.id)
                                                .update("Role", "Member").addOnSuccessListener {

                                                    // call the success1 method to pop up the success message
                                                    success1()
                                                    adapters.notifyDataSetChanged()
                                                    dialog.dismiss()
                                                }


                                        } else{
                                            Toast.makeText(requireContext(), "User is a $role , you cannot remove yourself because you are the group admin", Toast.LENGTH_SHORT).show()
                                            progressLoading.dismiss()

                                        }


                                    }
                                }

                            }.addOnFailureListener {
                                Toast.makeText(requireContext(), "Error removing member", Toast.LENGTH_SHORT).show()
                                progressLoading.dismiss()
                            }
                    }
                }.addOnSuccessListener {
                    Toast.makeText(requireContext(), "Error removing member", Toast.LENGTH_SHORT).show()
                    progressLoading.dismiss()
                }
            }

        }

        button2.setOnClickListener {
            dialog.dismiss()
        }
    }

    // This will change the user to moderator
    override fun onEdit(position: Int) {
        // get the username position on the list
        val usernameVal = memberList[position].username

        // Custom alert dialog below
        val builder = android.app.AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.message_layout2, null)
        val titleText = dialogLayout.findViewById<TextView>(R.id.dialog_title)
        val messageText = dialogLayout.findViewById<TextView>(R.id.dialog_message)
        val button = dialogLayout.findViewById<Button>(R.id.dialog_button)
        button.text = buildString {
            append("Set to Moderator")
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
            append("Set to Moderator")
        }
        messageText.text = buildString {
            append("Are you sure you want to set this user as a moderator")
            append(usernameVal)
            append("?")
        }

        val dialog = builder.setView(dialogLayout).create()

        dialog.show()

        button.setOnClickListener{

            // Load the progress dialog
            progressLoading = ProgressDialog(requireContext())
            progressLoading.setTitle("Loading")
            progressLoading.setMessage("Wait while loading...")
            progressLoading.setCanceledOnTouchOutside(false)
            progressLoading.show()

            // check if username is the same as the current user
            if(usernameVal == username){
                Toast.makeText(requireContext(), "You cannot change yourself to group moderator", Toast.LENGTH_SHORT).show()
                progressLoading.dismiss()
            }else{

                // Find the group name from the group collecitons
                db.collection("Group").whereEqualTo("GroupName", groupNameIntent).get().addOnSuccessListener { groupit ->
                    for (groupdocument in groupit) {

                        // then get the group id then convert it to string
                        val groupIDsubString = groupdocument.data["GroupID"].toString()
                        groupid = Integer.parseInt(groupIDsubString)

                        // Find the group members where equal to group id
                        db.collection("GroupMembers").whereEqualTo("GroupID", groupid).get()
                            .addOnSuccessListener { membersit ->
                                for (memberdocument in membersit) {

                                    // load the user id and get it
                                    val memberID = memberdocument.data["UserID"].toString()
                                    val role = memberdocument.data["Role"].toString()

                                    // This will check if member id is equal to retrieve id on the collection
                                    if (memberID == memberList[position].userid) {

                                        // This will check if the user is not a group admin or group moderator, if not it will change the role
                                        if (role!= "Group_Admin" || role!= "Group_Moderator"){

                                            // update the role of the user
                                            db.collection("GroupMembers").document(memberdocument.id)
                                                .update("Role", "Group_Moderator").addOnSuccessListener {

                                                    // call the success1 method to pop up the success message
                                                    success1()
                                                    adapters.notifyDataSetChanged()
                                                    dialog.dismiss()
                                                }


                                        } else{
                                            Toast.makeText(requireContext(), "User is already a $role", Toast.LENGTH_SHORT).show()
                                            progressLoading.dismiss()

                                        }


                                    }
                                }

                            }.addOnFailureListener {
                                Toast.makeText(requireContext(), "Error removing member", Toast.LENGTH_SHORT).show()
                                progressLoading.dismiss()
                            }
                    }
                }.addOnSuccessListener {
                    Toast.makeText(requireContext(), "Error removing member", Toast.LENGTH_SHORT).show()
                    progressLoading.dismiss()
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
            append("Group Alert")
        }
        messageText1.text = buildString {
            append("User has been removed from the group")
        }

        val dialog1 = builder1.setView(dialogLayout1).create()

        dialog1.show()
        button1.setOnClickListener{
            dialog1.dismiss()
        }
    }

    // Success message for changing the user to moderator
    private fun success1(){

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
            append("Group Alert")
        }
        messageText1.text = buildString {
            append("User has succesfully changed to moderator")
        }

        val dialog1 = builder1.setView(dialogLayout1).create()

        dialog1.show()
        button1.setOnClickListener{
            dialog1.dismiss()
        }
    }
}