package com.talatkuyuk.myexpenses.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.talatkuyuk.myexpenses.data.model.Converter
import com.talatkuyuk.myexpenses.utils.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

// https://arkapp.medium.com/how-to-use-shared-preferences-the-easy-and-fastest-way-98ce2013bf51

class PreferenceRepository(val context: Context) {

    // The others uses special SharedPreferences file
    private val pref: SharedPreferences = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
    private val editor = pref.edit()
    private val gson = Gson()


    private fun String.put(long: Long) {
        editor.putLong(this, long)
        editor.commit()
    }

    private fun String.put(int: Int) {
        editor.putInt(this, int)
        editor.commit()
    }

    private fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    private fun String.put(boolean: Boolean) {
        editor.putBoolean(this, boolean)
        editor.commit()
    }

    private fun String.getLong() = pref.getLong(this, 0)

    private fun String.getInt() = pref.getInt(this, 0)

    private fun String.getString() = pref.getString(this, "")!!

    private fun String.getBoolean() = pref.getBoolean(this, false)


    fun setIsVisitedOnboarding(isVisitedOnboarding: Boolean) {
        PREF_IS_VISITED_ONBOARDING.put(isVisitedOnboarding)
    }

    fun getIsVisitedOnboarding() = PREF_IS_VISITED_ONBOARDING.getBoolean()

    fun setConverter(converter: Converter) {
        val str: String = Json.encodeToString(converter)
        PREF_CONVERTER.put(str)
    }

    fun getConverter(): Converter {
        PREF_CONVERTER.getString().also {
            return if (it == "" || it.isEmpty())
                Converter.neutral
            else
                Json.decodeFromString<Converter>(PREF_CONVERTER.getString())
        }
    }

    fun setLastTime(date: Date) {
        PREF_LAST_TIME.put(gson.toJson(date))
    }

    fun getLastTime(): Date? {
        PREF_LAST_TIME.getString().also {
            return if (it.isNotEmpty())
                gson.fromJson(PREF_LAST_TIME.getString(), Date::class.java)
            else
                null
        }
    }

    fun clearData() {
        editor.clear()
        editor.commit()
    }



    // The Settings Page uses default SharedPreferences (for AndroidX Preference Library)
    private val prefX: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    // only get, because the Preference Screen writes itself
    private fun String.getStringX() = prefX.getString(this, "")!!

    fun getName() = PREF_NAME.getStringX()

    fun getGender() = PREF_GENDER.getStringX()

}