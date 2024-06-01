package com.example.efficiencymaster

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var usernametext: TextView

    private lateinit var nametext: TextView

    private lateinit var leveltext: TextView

    private lateinit var progresstext: TextView

    private lateinit var userImage: ImageView

    private lateinit var fragment:Fragment

    private lateinit var bundle:Bundle

    var username = ""

    val db = Firebase.firestore

    private val networkManager = NetworkManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Create an instance of SessionManager
        val sessionManager = SessionManager(this)

        // This will check if user is connected to the internet.
        networkManager.checkNetworkAndExitIfNotAvailable(this)

        // get the intent
        val intent1 = intent
        username = intent1.getStringExtra("username").toString()
        loadUserStats()

        // Find the id of naview
        val navigationView = findViewById<NavigationView>(R.id.navView)

        //Get the id's of the navheader components
        usernametext = navigationView.getHeaderView(0).findViewById(R.id.username)
        nametext = navigationView.getHeaderView(0).findViewById(R.id.name)
        userImage = navigationView.getHeaderView(0).findViewById(R.id.user_icon)
        progresstext = navigationView.getHeaderView(0).findViewById(R.id.progress_id)
        leveltext = navigationView.getHeaderView(0).findViewById(R.id.levelid)


        // This is the navigation view listener.
        navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {

                // This will go to home
                R.id.home -> {
                   val builder = AlertDialog.Builder(this)
                    builder.setTitle("Home")
                    builder.setMessage("Welcome to Home")
                    builder.setPositiveButton("OK"){ _, _ ->}
                    builder.show()

                    // This will go to home fragment
                    fragment = HomeFragmentation()
                    bundle = Bundle()
                    bundle.putString("username", username)
                    fragment.arguments = bundle
                    replaceFragment(fragment)

                    // This wil close the drawer of the navigation view
                    closeDrawer()


                    true
                }

                // This will go to your task fragment
                R.id.create ->{

                    // This will go to create task
                    fragment = InvidividualTask()
                    bundle = Bundle()
                    bundle.putString("username", username)
                    fragment.arguments = bundle
                    replaceFragment(fragment)

                    // This will close the drawer
                    closeDrawer()
                    true
                }

                R.id.group ->{

                    // This will go to group fragment
                    fragment = GroupFragment()
                    bundle = Bundle()
                    bundle.putString("username", username)
                    fragment.arguments = bundle
                    replaceFragment(fragment)

                    // This will close the drawer
                    closeDrawer()

                    true
                }
                R.id.profile ->{

                    // This will go to profile fragment
                    fragment = ProfileFragment()
                    bundle = Bundle()
                    bundle.putString("username", username)
                    fragment.arguments = bundle
                    replaceFragment(fragment)
                    closeDrawer()

                    true
                }
                R.id.Feed ->{

                    // This will go to feed fragment
                    closeDrawer()
                    true
                }
                R.id.leadeboards  ->{

                    // This will go to leaderboards fragment
                    closeDrawer()
                    true
                }
                    // This will go to achievements
                R.id.achievements ->{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Achievements")
                    builder.setMessage("Welcome to Achievements")
                    builder.setPositiveButton("OK"){_, _ ->}
                    builder.show()

                    // This will go to achievements fragment
                    closeDrawer()
                    true
                }

                // This will go to profile
                R.id.user ->{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("User")
                    builder.setMessage("Welcome to User")
                    builder.setPositiveButton("OK"){ _, _ ->}
                    builder.show()


                    // This will go to user fragment
                    closeDrawer()
                    true
                }

                // This will go to logout and go back to login
                R.id.logout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Logout")
                    builder.setMessage("Are you sure you want to logout?")
                    builder.setPositiveButton("Yes"){_, _ ->
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                        // Log out the user
                        sessionManager.logOut()

                        // This will
                        closeDrawer()
                    }
                    builder.setNegativeButton("No"){_, _ ->}
                    builder.show()
                    true
                }

                else -> false
            }

            }

        loadHome()
    }

    // This will load the home fragment
    private fun loadHome() {
        val homeFragmentation = HomeFragmentation()
        val bundle = Bundle()
        bundle.putString("username", username)
        homeFragmentation.arguments = bundle
        replaceFragment(homeFragmentation)
    }


    // Replace the fragment
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // This is used to open the navbar menu
     fun openDrawer(){
         val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
         drawerLayout.openDrawer(GravityCompat.START)
     }

    //  This will close the drawer menu
    fun closeDrawer(){
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    // This method used for loading the user stats.
    fun loadUserStats(){

        // Check if the user exists before load it
        db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener {
            if (it.isEmpty) {
                usernametext.text = buildString {
                    append("User does not exist")
                }
            }else{
                for (userdocument in it){

                    // Retrieve the variables from the document
                    val username = userdocument.data["username"].toString()
                    val iD = userdocument.data["UserID"].toString()

                    // This will load the user details and check if the user has any progress
                    db.collection("UserDetails").whereEqualTo("UserID", iD).get().addOnSuccessListener { userit->
                        for (document in userit){

                            // Retrieve the variables from the document
                            val image = document.data["imageurl"].toString()
                            val name = document.data["name"].toString()

                            // insert the retrieve value to the TextView.
                            usernametext.text = buildString {
                            append("Username:")
                            append(username)
                        }
                            nametext.text = buildString {
                            append("Name:")
                            append(name)
                        }

                            // Set the retrieve Imageurl to image
                            Glide.with(this).load(image).into(userImage)

                        }
                    }
                    // Check if user has any progress
                    db.collection("ProgresssUser").whereEqualTo("UserID", iD).get().addOnSuccessListener { progressit ->
                        if (progressit.isEmpty){
                            progresstext.text = buildString {
                            append("Progress:0%")

                                leveltext.text = buildString {
                                    append("Level: unknown")
                                }
                        }
                            AlertDialog.Builder(this)
                                .setTitle("Progress")
                                .setMessage("You have no progress")
                                .setPositiveButton("OK"){_, _ ->}
                                .show()
                        }else{

                            // Retrieve the progress of the user
                            for (document in progressit){

                                //  get the progress xp
                                val progress = document.getLong("ProgressXp") // Retrieve as Long
                                levels(progress)
                                progresstext.text = buildString {
                                append("Progress: ")
                                append(progress.toString())
                                append(" xp")
                            }
                            }
                        }
                    }
                }
            }
        }
    }

    // This will load a levels
    private fun levels(progress: Long?) {

        // Check if the progress is not null
        if (progress != null) {

            // This will check the progress of the user and assign a level to the user
            if (progress in 0..10000){
                leveltext.text = buildString {
                    append("Level: 1")
                }

                // This will check the progress of the user and assign a level to the user
            }else if (progress in 10000..25000){
                leveltext.text = buildString {
                    append("Level: 2")
                }

                // This will check the progress of the user and assign a level to the user
            }else if (progress in 26000..35000){
                leveltext.text = buildString {
                    append("Level: 3")
                }

                // This will check the progress of the user and assign a level to the user
            }else if (progress in 36000..45000){
                leveltext.text = buildString {
                    append("Level: 4")
                }

                // This will check the progress of the user and assign a level to the user
            }else if (progress in 46000..55000){
                leveltext.text = buildString {
                    append("Level: 5")
                }

                // This will check the progress of the user and assign a level to the user
            }else if (progress in 56000..65000){
                leveltext.text = buildString {
                    append("Level: 6")
                }

                // This will check the progress of the user and assign a level to the user
            }else if (progress in 66000..75000){
                leveltext.text = buildString {
                    append("Level: 7")
                }

                // This will check the progress of the user and assign a level to the user
            }else if (progress in 76000..85000){
                leveltext.text = buildString {
                    append("Level: 8")
                }

                // This will check the progress of the user and assign a level to the user
            }else if (progress in 86000..95000){
                leveltext.text = buildString {
                    append("Level: 9")
                }

                // This will check the progress of the user and assign a level to the user
            }else if (progress in 96000..100000){
                leveltext.text = buildString {
                    append("Level: 10")
                }
            }
            else if (progress in 100000..110000){
                leveltext.text = buildString {
                    append("Level: 11")
                }
            }

            // This will check the progress of the user and assign a level to the user
            else if (progress in 110000..120000){
                leveltext.text = buildString {
                    append("Level: 12")
                }
            }

            // This will check the progress of the user and assign a level to the user
            else if (progress in 120000..130000){
                leveltext.text = buildString {
                    append("Level: 13")
                }
            }

            // This will check the progress of the user and assign a level to the user
            else if (progress in 130000..140000){
                leveltext.text = buildString {
                    append("Level: 14")
                }
            }

            // This will check the progress of the user and assign a level to the user
            else if (progress in 140000..150000){
                leveltext.text = buildString {
                    append("Level: 15")
                }
            }
            else if (progress in 150000..160000){
                leveltext.text = buildString {
                    append("Level: 16")
                }
            }

            // This will check the progress of the user and assign a level to the user
            else if (progress in 160000..170000){
                leveltext.text = buildString {
                    append("Level: 17")
                }
            }

            // This will check the progress of the user and assign a level to the user
            else if (progress in 170000..180000){
                leveltext.text = buildString {
                    append("Level: 18")
                }
            }

            // This will check the progress of the user and assign a level to the user
            else if (progress in 180000..190000){
                leveltext.text = buildString {
                    append("Level: 19")
                }
            }

            // This will check the progress of the user and assign a level to the user
            else if (progress in 190000..200000){
                leveltext.text = buildString {
                    append("Level: 20")
                }
            }
        }

    }
}