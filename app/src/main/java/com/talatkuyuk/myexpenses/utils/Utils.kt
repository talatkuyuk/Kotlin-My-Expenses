package com.talatkuyuk.myexpenses.utils

import android.app.Activity
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
}