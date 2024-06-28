package com.example.week1.ui.breadfeed

import androidx.lifecycle.ViewModel

/* BreadfeedViewModel manages the data for BreadfeedFragment.
 * It holds a list of bread posts.
 */
class BreadfeedViewModel : ViewModel() {

    val breadPosts: List<BreadPost> = DummyData.getDummyPosts()

}