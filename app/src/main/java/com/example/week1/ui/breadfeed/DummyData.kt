package com.example.week1.ui.breadfeed

import android.util.Log
/* DummyData provides a list of sample bread posts for testing and development purposes.
 * Each BreadPost contains a sample image URL and description.
 */
object DummyData {
    fun getDummyPosts(): List<BreadPost> {
        // Generate a list of dummy BreadPost objects
        Log.d("DummyData", "getDummyPosts: Generating dummy posts")
        return listOf(
            BreadPost("https://via.placeholder.com/150", "Delicious bread from ABC Bakery."),
            BreadPost("https://via.placeholder.com/150", "Tasty croissants at XYZ Cafe."),
            BreadPost("https://via.placeholder.com/150", "Freshly baked bagels from Baker's Street."),
            BreadPost("https://via.placeholder.com/150", "Sweet donuts from Donut Delight."),
            BreadPost("https://via.placeholder.com/150", "Savory muffins from Morning Glory."),
            BreadPost("https://via.placeholder.com/150", "Soft and fluffy bread from Bread Heaven."),
            BreadPost("https://via.placeholder.com/150", "Crispy baguettes at Parisian Bakery."),
            BreadPost("https://via.placeholder.com/150", "Chocolate croissants from Cocoa Corner."),
            BreadPost("https://via.placeholder.com/150", "Warm and tasty scones from Tea Time."),
            BreadPost("https://via.placeholder.com/150", "Artisan bread from Rustic Bakery.")
        )
    }
}