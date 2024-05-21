package classes
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AlertDialog
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
            AlertDialog.Builder(context)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("Exit") { _, _ ->
                    // This will close the app
                    (context as Activity).finishAffinity()
                }
                .show()
        }
    }
}