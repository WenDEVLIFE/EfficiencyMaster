package com.example.efficiencymaster

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var usernametext: TextView

    lateinit var nametext: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navigationView = findViewById<NavigationView>(R.id.navView)
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

                // This will go to create task
                R.id.create ->{
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Create")
                    builder.setMessage("Welcome to Create")
                    builder.setPositiveButton("OK"){dialog, which ->}
                    builder.show()
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

     fun OpenDrawer(){
         val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
         drawerLayout.openDrawer(GravityCompat.START)
     }
}