package apw.android.hubspace.backend.data

enum class FeedType {
    ANNOUNCEMENT,
    EVENT,
    ACTIVITY
}

data class FeedItem (
    val id: String,
    val title: String,
    val body: String,
    val postedBy: String,
    val timeStamp: String,
    val type: FeedType = FeedType.ACTIVITY,
    val isPinned: Boolean = false
)