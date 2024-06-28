package com.example.week1.ui.breadfeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/* BreadfeedViewModel manages the data for BreadfeedFragment.
 * It holds a list of bread posts.
 */
class BreadfeedViewModel : ViewModel() {

    private val _breadPosts = MutableLiveData<List<BreadPost>>().apply {
        value = DummyData.getDummyPosts()
    }
    val breadPosts: LiveData<List<BreadPost>> get() = _breadPosts

    fun addBreadPost(breadPost: BreadPost) {
        val updatedPosts = _breadPosts.value.orEmpty().toMutableList().apply {
            add(0, breadPost)  // Add the new post at the beginning of the list
        }
        _breadPosts.value = updatedPosts
    }
}