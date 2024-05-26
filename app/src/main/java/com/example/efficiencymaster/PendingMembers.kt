package com.example.efficiencymaster

import adapters.PendingAdapter
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
import classes.MembersPending
import com.bumptech.glide.Glide
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
class PendingMembers : Fragment(), PendingAdapter.OnDeleteListener, PendingAdapter.OnEditListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerView:RecyclerView
    lateinit var progressLoading:ProgressDialog
    lateinit var adapters:PendingAdapter
    val db = Firebase.firestore


    var username =""
    var groupNameIntent=""
    var memberList = mutableListOf<MembersPending>()
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
        memberList = ArrayList()
        adapters = PendingAdapter(memberList)
        recyclerView.adapter = adapters
        adapters.setOnDeleteListener(::onDelete)
        adapters.setOnEditListener(::onEdit)
        loadPendingMembers()



        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadPendingMembers(){

        // This will get the group id
        db.collection("Group").whereEqualTo("GroupName",groupNameIntent).get().addOnSuccessListener { documents ->
            for (document in documents){

                // This will get the group id
                val groupid = document.data["GroupID"].toString()

                // This will get the pending members
                db.collection("PendingGroupMembers").whereEqualTo("GroupID",groupid).get().addOnSuccessListener { pendingit ->
                    for (pendingdocs in pendingit){
                        // This will get the user id, status and date request
                           val userid= pendingdocs.data["UserID"].toString()
                           val status = pendingdocs.data["Status"].toString()
                           val dateRequest = pendingdocs.data["Date Requested"].toString()

                        // This will get the username
                        db.collection("User").whereEqualTo("UserID",userid).get().addOnSuccessListener { userit ->
                            for (userdocs in userit){

                                // This will get the username
                                val username = userdocs.data["username"].toString()

                                // add the member to the list
                                val member = MembersPending(username,groupNameIntent,status,dateRequest)
                                memberList.add(member)
                            }
                            // notify the adapters
                            adapters.notifyDataSetChanged()
                        }

                    }
                }
            }
        }
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
         * @return A new instance of fragment MembersPending.
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onDelete(position: Int) {
       val member = memberList[position]
            val memberName = member.username
            val groupName = member.groupName
            val status = member.status
            val dateRequest = member.dateRequest

        // Custom alert dialog below
        val builder = android.app.AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.message_layout2, null)
        val titleText = dialogLayout.findViewById<TextView>(R.id.dialog_title)
        val messageText = dialogLayout.findViewById<TextView>(R.id.dialog_message)
        val button = dialogLayout.findViewById<Button>(R.id.dialog_button)
        button.text = buildString {
            append("Deny User")
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
            append("Denied to the group")
        }
        messageText.text = buildString {
            append("Are you sure you want to denied this user to be a member ")
            append(memberName)
            append("?")
        }

        val dialog = builder.setView(dialogLayout).create()

        dialog.show()

        button.setOnClickListener{

            // Load the progress dialog
            progressLoading = ProgressDialog(requireContext())
            progressLoading.setTitle("Denying User from Group")
            progressLoading.setMessage("Wait while loading...")
            progressLoading.setCanceledOnTouchOutside(false)
            progressLoading.show()


            // This will get the group id
            db.collection("Group").whereEqualTo("GroupName",groupName).get().addOnSuccessListener { documents ->
                for (document in documents) {

                    // This will get the group id
                    val groupid = document.data["GroupID"].toString()

                    // This will get the user id
                    db.collection("User").whereEqualTo("username", memberName).get()
                        .addOnSuccessListener { userit ->
                            for (userdocs in userit) {

                                // This will get the user id
                                val userid = userdocs.data["UserID"].toString()

                                // This will get the pending members
                                db.collection("PendingGroupMembers")
                                    .whereEqualTo("GroupID", groupid).whereEqualTo("UserID", userid)
                                    .whereEqualTo("Status", status)
                                    .whereEqualTo("Date Requested", dateRequest).get()
                                    .addOnSuccessListener { pendingit ->
                                        for (pendingdocs in pendingit) {

                                            // This will delete the pending member
                                            db.collection("PendingGroupMembers")
                                                .document(pendingdocs.id).delete()
                                                .addOnSuccessListener {
                                                    memberList.removeAt(position)
                                                    adapters.notifyDataSetChanged()
                                                    progressLoading.dismiss()
                                                    success()
                                                    dialog.dismiss()
                                                }
                                        }
                                    }
                            }
                        }
                }
            }
        }

        button2.setOnClickListener {
            dialog.dismiss()
        }

    }

    override fun onEdit(position: Int) {

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
            append("User has been denied from the group")
        }

        val dialog1 = builder1.setView(dialogLayout1).create()

        dialog1.show()
        button1.setOnClickListener{
            dialog1.dismiss()
        }
    }

}