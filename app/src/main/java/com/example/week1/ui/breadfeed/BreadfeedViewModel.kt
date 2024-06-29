package com.example.week1.ui.breadfeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BreadfeedViewModel : ViewModel() {

    private val _breadPosts = MutableLiveData<List<BreadPost>>().apply {
        value = BreadfeedDummyData.getBreadPosts()
    }
    val breadPosts: LiveData<List<BreadPost>> = _breadPosts

    fun addBreadPost(breadPost: BreadPost) {
        val currentPosts = _breadPosts.value ?: emptyList()
        _breadPosts.value = listOf(breadPost) + currentPosts
    }
}