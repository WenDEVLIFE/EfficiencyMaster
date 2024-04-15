package com.example.efficiencymaster

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var usernametext: TextView

    lateinit var emailtext: TextView
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
                    bundle.putString("email", emailtext.text.toString())
                    homeFragmentation.arguments = bundle
                    replaceFragment(homeFragmentation)


                    true
                }
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
        emailtext = navigationView.getHeaderView(0).findViewById(R.id.email)

    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}