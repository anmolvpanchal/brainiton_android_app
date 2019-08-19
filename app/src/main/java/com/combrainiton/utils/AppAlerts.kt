package com.combrainiton.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast

/**
 * Created by Dipendra Sharma  on 23-08-2018.
 */

class AppAlerts {

    fun show(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(container: View, msg: String) {
        Snackbar.make(container, msg, Snackbar.LENGTH_LONG).show()
    }

    fun showSnackBarWithAction(container: View, msg: String, btnTitle: String, onClickListener: View.OnClickListener) {
        val snackbar = Snackbar.make(container, msg, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(btnTitle, onClickListener)
        snackbar.show()
    }

    fun showAlertWithAction(mContext: Context, title: String, msg: String, actionString: String, negativeString: String, clickListener: DialogInterface.OnClickListener, isSetNegativeButton: Boolean) {
        val builder = AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(actionString, clickListener)
        if (isSetNegativeButton)
            builder.setNegativeButton(negativeString, null)
        builder.create().show()
    }

    fun showAlertMessage(mContext: Context, title: String, msg: String) {
        val ac: Activity = mContext as Activity
        if (!ac.isFinishing && !ac.isDestroyed) {
            val builder = AlertDialog.Builder(mContext)
                    .setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton("OK", null)
            builder.create().show()
        }
    }

}