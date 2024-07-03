data class ReviewItem(
    val breadName: String,
    val reviewContent: String,
    val myRating: Float,
    val photoPath: String?,
    val timestamp: Long // 새로운 필드 추가
)