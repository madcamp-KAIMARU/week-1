package com.example.week1.ui.breadfeed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BreadfeedViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = BreadfeedPreferences(application)

    private val _breadPosts = MutableLiveData<List<BreadPost>>().apply {
        value = preferences.getBreadPosts()
    }
    val breadPosts: LiveData<List<BreadPost>> = _breadPosts

    fun addBreadPost(newPost: BreadPost) {
        val currentPosts = _breadPosts.value?.toMutableList() ?: mutableListOf()
        currentPosts.add(0, newPost)
        _breadPosts.value = currentPosts
        preferences.saveBreadPosts(currentPosts)
    }

    fun updateBreadPost(updatedPost: BreadPost) {
        val currentPosts = _breadPosts.value?.toMutableList() ?: mutableListOf()
        val index = currentPosts.indexOfFirst { it.id == updatedPost.id }
        if (index != -1) {
            currentPosts[index] = updatedPost
            _breadPosts.value = currentPosts
            preferences.saveBreadPosts(currentPosts)
        }
    }
}