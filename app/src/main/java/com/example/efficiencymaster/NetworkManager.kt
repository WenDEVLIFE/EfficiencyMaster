package com.example.efficiencymaster
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.efficiencymaster.R

class NetworkManager {

    // This will check if the internet is available
    private fun isNetworkAvailable(context: Context): Boolean {

        // Get the connectivity manager
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Check if the device is running on Android M or higher
        val network = connectivityManager.activeNetwork ?: return false

        // Get the network capabilities
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        // Check if the device is connected to wifi or mobile data
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true // wifi
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true // cellular data
            else -> false
        }
    }

    // This method is used to check if the internet connection is available and exit the app if not
    fun checkNetworkAndExitIfNotAvailable(context: Context) {
        if (!isNetworkAvailable(context)) {
            networkError(context)
        }

    }

    private fun networkError(context: Context){
        // below are the customize alert dialog components and etc.
        val builder1 = android.app.AlertDialog.Builder(context)
        val inflater1 = (context as Activity).layoutInflater
        val dialogLayout1 = inflater1.inflate(R.layout.message_layout, null)
        val titleText1= dialogLayout1.findViewById<TextView>(R.id.dialog_title)
        val messageText1 = dialogLayout1.findViewById<TextView>(R.id.dialog_message)
        val button1 = dialogLayout1.findViewById<Button>(R.id.dialog_button)
        button1.text = buildString {
            append("Close the application")
        }
        val imageView2 = dialogLayout1.findViewById<ImageView>(R.id.imageView2)

        Glide.with(context)
            .asDrawable()
            .load(R.drawable.no_internet)
            .into(imageView2)
        imageView2.scaleType = ImageView.ScaleType.FIT_CENTER
        val params1 = imageView2.layoutParams
        val scale1 = context.resources.displayMetrics.density
        params1.width = (100 * scale1).toInt()
        params1.height = (100 * scale1).toInt()
        imageView2.layoutParams = params1
        titleText1.text = buildString {
            append("Network Error")
        }
        messageText1.text = buildString {
            append("No Internet Access, Please check your internet connection and try again.")
        }

        val dialog1 = builder1.setView(dialogLayout1).create()

        dialog1.show()
        button1.setOnClickListener{
            dialog1.dismiss()
            (context as Activity).finish()
        }
    }
}