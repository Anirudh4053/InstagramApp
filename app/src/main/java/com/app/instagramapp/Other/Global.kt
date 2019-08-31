package com.app.instagramapp.Other

import android.Manifest
import android.content.Context

const val FRAGMENT_CAMERA_REQUEST = 99
const val TEMP_FILES = "instatest"
const val ERROR = "Error"
const val FILE_PROVIDER_AUTHORITY = "com.app.instagramapp"
const val REQUEST_PERMISSION_SETTING = 103
const val PERMISSION_CALLBACK_CONSTANT = 102
const val CROP_REQUEST = 50
const val GALLERY_REQUEST = 51
const val PERMISSION_REQUIRED = Manifest.permission.CAMERA


//companion object {

val PREFS_NAME = "LOGIN"
fun setPermissionRequest(context: Context, value: Boolean) {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putBoolean(PERMISSION_REQUIRED, value)
    editor.apply()
}

fun getPermissionRequest(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(PERMISSION_REQUIRED, false)
}
fun setUserName(context: Context, value: String) {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString("username", value)
    editor.apply()
}

fun getUserName(context: Context): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getString("username", "")
}
//}