package com.combrainiton.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Ronak on 3/21/2016.
 */

@Suppress("unused")
class AppSharedPreference(var mContext: Context) {

    fun saveString(key: String, value: String): Boolean {
        return try {
            val sharedPreference: SharedPreferences = mContext.getSharedPreferences("atm", 0)
            val editor = sharedPreference.edit()
            editor.putString(key, value)
            editor.apply()
            true
        } catch (e: Exception) {
            false
        }

    }

    fun getString(key: String): String {
        var value: String? = ""
        return try {
            val sharedPreferences = mContext.getSharedPreferences("atm", 0)
            value = sharedPreferences.getString(key, "")
            if (value == "") {
                value = ""
                value

            } else {
                value
            }

        } catch (e: Exception) {
            e.printStackTrace()
            value!!
        }

    }

    fun saveInt(key: String, value: Int): Boolean {
        return try {
            val sharedPreferences = mContext.getSharedPreferences("atm", 0)
            val editor = sharedPreferences.edit()
            editor.putInt(key, value)
            editor.apply()
            true
        } catch (e: Exception) {
            false

        }

    }

    fun getInt(key: String): Int {
        var value = 0
        return try {
            val sharedPreferences = mContext.getSharedPreferences("atm", 0)
            value = sharedPreferences.getInt(key, 0)
            if (value == 0) {
                value = 0
                value

            } else {
                value
            }

        } catch (e: Exception) {
            e.printStackTrace()
            value
        }

    }

    fun saveBoolean(key: String, value: Boolean): Boolean {
        return try {
            val sharedPreferences = mContext.getSharedPreferences("atm", 0)
            val editor = sharedPreferences.edit()
            editor.putBoolean(key, value)
            editor.apply()
            true
        } catch (e: Exception) {
            false

        }

    }

    fun getBoolean(key: String): Boolean {
        var value = false
        return try {
            val sharedPreferences = mContext.getSharedPreferences("atm", 0)
            value = sharedPreferences.getBoolean(key, false)
            value

        } catch (e: Exception) {
            e.printStackTrace()
            value
        }

    }

    fun saveLong(key: String, value: Long): Boolean {
        return try {
            val sharedPreferences = mContext.getSharedPreferences("atm", 0)
            val editor = sharedPreferences.edit()
            editor.putLong(key, value)
            editor.apply()
            true
        } catch (e: Exception) {
            false
        }

    }

    fun getLong(key: String): Long {
        var value: Long = 0
        return try {
            val sharedPreferences = mContext.getSharedPreferences("atm", 0)
            value = sharedPreferences.getLong(key, 0)
            value

        } catch (e: Exception) {
            e.printStackTrace()
            value
        }

    }

    @SuppressLint("ApplySharedPref")
    fun deleteSharedPreference() {
        val sharedPreferences = mContext.getSharedPreferences("atm", 0)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()
    }

    fun saveBooleanDatabase(key: String, value: Boolean): Boolean {
        return try {
            val sharedPreferences = mContext.getSharedPreferences("ormLite", 0)
            val editor = sharedPreferences.edit()
            editor.putBoolean(key, value)
            editor.apply()
            true
        } catch (e: Exception) {
            false

        }

    }

    @SuppressWarnings("unused")
    fun getBooleanDatabase(key: String): Boolean {
        var value = false
        return try {
            val sharedPreferences = mContext.getSharedPreferences("ormLite", 0)
            value = sharedPreferences.getBoolean(key, false)
            value

        } catch (e: Exception) {
            e.printStackTrace()
            value
        }

    }

    @SuppressWarnings("unused")
    fun saveDatabaseString(key: String, value: String): Boolean {
        return try {
            val sharedPreferences = mContext.getSharedPreferences("ormLite", 0)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
            true
        } catch (e: Exception) {
            false

        }

    }

    @SuppressWarnings("unused")
    fun getDatabaseString(key: String): String {
        var value: String? = ""
        return try {
            val sharedPreferences = mContext.getSharedPreferences("ormLite", 0)
            value = sharedPreferences.getString(key, "")
            if (value == "") {
                value = ""
                value

            } else {
                value
            }

        } catch (e: Exception) {
            e.printStackTrace()
            value!!
        }
    }

}
