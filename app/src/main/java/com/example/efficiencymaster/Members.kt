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
        adapters.setOnCancelListener(::onCancel)
        adapters.setOnDeleteListener(::onDelete)
        adapters.setOnEditListener(::onEdit)
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

    override fun onDelete(position: Int) {
        val usernameVal = memberList[position].username
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
            append("Are you sure you remove this member from the group?")
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
    override fun onCancel(position: Int) {
        Toast.makeText(requireContext(), "Cancel", Toast.LENGTH_SHORT).show()
    }

    override fun onEdit(position: Int) {
        Toast.makeText(requireContext(), "Edit", Toast.LENGTH_SHORT).show()
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
}