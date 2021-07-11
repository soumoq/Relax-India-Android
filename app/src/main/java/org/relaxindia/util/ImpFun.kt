package org.relaxindia.util

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog

object ImpFun {

    const val locationAlert = "We'll only use your location to show available delivery option. You can change this preference in settings at any time"


    fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            locationMode = try {
                Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE)
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            !TextUtils.isEmpty(locationProviders)
        }
    }

    fun openLocationDialog(context: Context, title: String, message: String) {
        // setup the alert builder
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        // add a button
        builder.setPositiveButton("Allow Access", DialogInterface.OnClickListener { dialog, which ->
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        })


        val dialog = builder.create()
        dialog.show()
    }

    fun openDialog(context: Context, title: String, message: String) {
        //val intent = Intent(context, TripAnalyzeActivity::class.java)
        //intent.putExtra("fileName", fileName)
        //context.startActivity(intent)


        // setup the alert builder
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        // add a button
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->

        })

        // create and show the alert dialog

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

}