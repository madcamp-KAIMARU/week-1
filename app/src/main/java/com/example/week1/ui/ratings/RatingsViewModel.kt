package com.example.week1.ui.ratings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RatingsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is ratings Fragment"
    }
    val text: LiveData<String> = _text
}