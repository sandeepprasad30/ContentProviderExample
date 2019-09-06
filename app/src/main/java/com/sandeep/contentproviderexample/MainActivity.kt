package com.sandeep.contentproviderexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

const val TAG = "MainActivity"
const val REQUEST_CODE_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {

    //private var readGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        Log.d(TAG, "onCreate check permissions $hasReadContactPermission")

        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            //readGranted = true
            Log.d(TAG, "onCreate permission granted")
        } else {
            Log.d(TAG, "onCreate requesting permission")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
        }

        fab.setOnClickListener { view ->
            Log.d(TAG, "fab click starts")
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    projection,
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                )

                val contacts = ArrayList<String>()
                cursor?.use{
                    while(it.moveToNext()){
                        contacts.add(it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                    }
                }

                val adapter = ArrayAdapter<String>(this, R.layout.contcat_details, R.id.name, contacts)
                contact_names.adapter = adapter
            } else {
                Snackbar.make(view, "Grant access to contacts", Snackbar.LENGTH_LONG)
                    .setAction("Grant Access") {
                        Log.d(TAG, "Snackbar: click start")
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                            Log.d(TAG, "Snackbar: callign req permission")
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_CONTACTS),
                                REQUEST_CODE_READ_CONTACTS
                            )
                        } else {
                            // user has permanently denied so take them to settings
                            Log.d(TAG, "Snackbar: launch settings")
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", this.packageName, null )
                            intent.data = uri
                            this.startActivity(intent)
                        }
                        Log.d(TAG, "Snackbar: click ends")

                    }.show()
            }


            Log.d(TAG, "fab click ends")
        }

        Log.d(TAG, "oncreated ends")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult : starts")

        when (requestCode) {
            REQUEST_CODE_READ_CONTACTS -> {
                //readGranted = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult : granted")
                    //true
                } else {
                    Log.d(TAG, "onRequestPermissionsResult refused")
                    //false
                }

                //fab.isEnabled = readGranted
            }
        }

        Log.d(TAG, "onRequestPermissionsResult : ends")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
