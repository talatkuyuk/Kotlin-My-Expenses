package com.talatkuyuk.myexpenses.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog

class Utils {

     public fun showDialogForExit(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Are you sure you want to exit?")
            .setTitle("You are about to exit!")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, which ->
                activity.finishAndRemoveTask()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    public fun showDialog(activity: Activity, message: String) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(message)
            .setTitle("Validation Error")
            .setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }
}