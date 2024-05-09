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

    lateinit var usernametext: TextView

    lateinit var nametext: TextView

    lateinit var progresstext: TextView

    lateinit var userImage: ImageView

    var username = ""

    val db = Firebase.firestore
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

        // get the intent
        val Intent = intent
        username = Intent.getStringExtra("username").toString()


        // Find the id of naview
        val navigationView = findViewById<NavigationView>(R.id.navView)

        //Get the id's of the navheader components
        usernametext = navigationView.getHeaderView(0).findViewById(R.id.username)
        nametext = navigationView.getHeaderView(0).findViewById(R.id.name)
        userImage = navigationView.getHeaderView(0).findViewById(R.id.user_icon)
        progresstext = navigationView.getHeaderView(0).findViewById(R.id.progress_id)
        LoadUserStats()

        // This is the navigation view listener.
        navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {

                // This will go to home
                R.id.home -> {
                   val builder = AlertDialog.Builder(this)
                    builder.setTitle("Home")
                    builder.setMessage("Welcome to Home")
                    builder.setPositiveButton("OK"){dialog, which ->}
                    builder.show()

                    // This will go to home fragment
                    val homeFragmentation = HomeFragmentation()
                    val bundle = Bundle()
                    bundle.putString("username", usernametext.text.toString())
                    bundle.putString("name", nametext.text.toString())
                    homeFragmentation.arguments = bundle
                    replaceFragment(homeFragmentation)


                    true
                }

                // This will go to your task fragment
                R.id.create ->{

                    // This will go to create task
                    val groupFragment = InvidividualTask()
                    val bundle = Bundle()
                    bundle.putString("username", username)
                    groupFragment.arguments = bundle
                    replaceFragment(groupFragment)
                    true
                }

                R.id.group ->{

                    // This will go to group fragment
                    val groupFragment = GroupFragment()
                    val bundle = Bundle()
                    bundle.putString("username", username)
                    groupFragment.arguments = bundle
                    replaceFragment(groupFragment)

                    true
                }
                R.id.profile ->{

                    true
                }
                R.id.Feed ->{

                    true
                }
                R.id.leadeboards  ->{

                    true
                }
                    // This will go to achievements
                R.id.achievements ->{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Achievements")
                    builder.setMessage("Welcome to Achievements")
                    builder.setPositiveButton("OK"){dialog, which ->}
                    builder.show()
                    true
                }

                // This will go to profile
                R.id.user ->{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("User")
                    builder.setMessage("Welcome to User")
                    builder.setPositiveButton("OK"){dialog, which ->}
                    builder.show()
                    true
                }

                // This will go to logout and go back to login
                R.id.logout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Logout")
                    builder.setMessage("Are you sure you want to logout?")
                    builder.setPositiveButton("Yes"){dialog, which ->
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                        // Log out the user
                        sessionManager.logOut()
                    }
                    builder.setNegativeButton("No"){dialog, which ->}
                    builder.show()
                    true
                }

                else -> false
            }

            }
        usernametext = navigationView.getHeaderView(0).findViewById(R.id.username)
        nametext = navigationView.getHeaderView(0).findViewById(R.id.name)


        loadHome()
    }

    // This will load the home fragment
    private fun loadHome() {
        val homeFragmentation = HomeFragmentation()
        val bundle = Bundle()
        bundle.putString("username", usernametext.text.toString())
        bundle.putString("name", nametext.text.toString())
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
     fun OpenDrawer(){
         val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
         drawerLayout.openDrawer(GravityCompat.START)
     }

    // This method used for loading the user stats.
    fun LoadUserStats(){

        // Check if the user exists before load it
        db.collection("User").whereEqualTo("username", username).get().addOnSuccessListener {
            if (it.isEmpty) {
                usernametext.text = "User does not exist"
            }else{
                for (document in it){

                    // Retrieve the variables from the document
                    val username = document.data["username"].toString()
                    val ID = document.data["UserID"].toString()

                    // This will load the user details and check if the user has any progress
                    db.collection("UserDetails").whereEqualTo("UserID", ID).get().addOnSuccessListener {
                        for (document in it){

                            // Retrieve the variables from the document
                            val image = document.data["imageurl"].toString()
                            val name = document.data["name"].toString()

                            // insert the retrieve value to the TextView.
                            usernametext.text = "Username:$username"
                            nametext.text = "Name:$name"

                            // Set the retrieve Imageurl to image
                            Glide.with(this).load(image).into(userImage)

                            // Check if user has any progress
                            db.collection("Progress").whereEqualTo("UserID", ID).get().addOnSuccessListener {
                                if (it.isEmpty){
                                    progresstext.text = "Progress:0%"
                                }else{
                                    for (document in it){
                                        val progress = document.data["progress"].toString()
                                        progresstext.text = "Progress: $progress%"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}