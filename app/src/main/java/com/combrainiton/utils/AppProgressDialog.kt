package com.combrainiton.utils

import android.content.Context
import dmax.dialog.SpotsDialog

class AppProgressDialog(private var mContext: Context) {

    //TODO remove this class and remove all of it's usage from the entire project.
    //why ?? because we are using this progress dialog before any acitvity starts which is not require in most of the cases.
    //we need progress dialog only when we are trying to get data from internet.
    //in live quiz acitvity,normal quiz acitivty and live poll activity multiple thread were accessing this progress bar in diffrent conditions.
    //which lead to run time errors that's why i had temporarily disabled it.
    //replace this progressdialog with progress bar.

    lateinit var dialog: SpotsDialog

    fun show() {
    /*  dialog = SpotsDialog(mContext)
      dialog.setCancelable(false)
     dialog.show()*/
    }

    fun hide() {
       /* if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        }*/
    }

} 