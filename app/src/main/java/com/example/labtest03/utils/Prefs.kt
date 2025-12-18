package com.example.labtest03.utils

import android.content.Context
import com.example.labtest03.models.Habit
import com.example.labtest03.models.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

object Prefs {
    private const val PREFS_NAME = "wellness_prefs"
    private const val KEY_HABITS = "habits"
    private const val KEY_MOODS = "moods"
    private const val KEY_HYDRATION_INTERVAL = "hydration_interval" // minutes
    private const val KEY_NOTIF_ENABLED = "notif_enabled"
    private val gson = Gson()

    private fun prefs(c: Context) = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Habits
    fun saveHabits(c: Context, list: List<Habit>) {
        prefs(c).edit { putString(KEY_HABITS, gson.toJson(list)) }
    }
    fun loadHabits(c: Context): MutableList<Habit> {
        val json = prefs(c).getString(KEY_HABITS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Habit>>() {}.type
        return gson.fromJson(json, type)
    }

    // Moods
    fun saveMoods(c: Context, list: List<MoodEntry>) {
        prefs(c).edit { putString(KEY_MOODS, gson.toJson(list)) }
    }
    fun loadMoods(c: Context): MutableList<MoodEntry> {
        val json = prefs(c).getString(KEY_MOODS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
        return gson.fromJson(json, type)
    }

    // Settings
    fun setHydrationInterval(c: Context, minutes: Int) = prefs(c).edit {
        putInt(
            KEY_HYDRATION_INTERVAL,
            minutes
        )
    }
    fun getHydrationInterval(c: Context) = prefs(c).getInt(KEY_HYDRATION_INTERVAL, 60) // default 60 minutes

    fun setNotificationsEnabled(c: Context, enabled: Boolean) = prefs(c).edit {
        putBoolean(
            KEY_NOTIF_ENABLED,
            enabled
        )
    }
    fun getNotificationsEnabled(c: Context) = prefs(c).getBoolean(KEY_NOTIF_ENABLED, true)
}
