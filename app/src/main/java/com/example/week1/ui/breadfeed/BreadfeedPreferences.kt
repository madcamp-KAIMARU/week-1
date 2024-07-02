package com.example.week1.ui.breadfeed

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BreadfeedPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("BreadfeedPrefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    fun saveBreadPosts(breadPosts: List<BreadPost>) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(breadPosts)
        editor.putString("breadPosts", json)
        editor.apply()
    }

    fun getBreadPosts(): List<BreadPost> {
        val json = sharedPreferences.getString("breadPosts", null) ?: return emptyList()
        val type = object : TypeToken<List<BreadPost>>() {}.type
        return gson.fromJson(json, type)
    }

    fun initializeWithDummyData(dummyData: List<BreadPost>) {
        if (getBreadPosts().isEmpty()) {
            saveBreadPosts(dummyData)
        }
    }
}