package apw.android.hubspace.backend

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

data class TeamInfo(
    val memberCount: Int,
    val memberList: List<String>
)

class HubSpaceRepository {
    private val database: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun getTeamMembersInfo(teamName: String): TeamInfo {
        return try {
            val document = database.collection("teams")
                .document(teamName)
                .get()
                .await()

            if (!document.exists()) {
                return TeamInfo(0, emptyList())
            }
            val members = when (val rawMembers = document.get("members")) {
                is List<*> -> rawMembers.filterIsInstance<String>()
                else -> emptyList()
            }

            TeamInfo(members.size, members)
        } catch (e: Exception) {
            Log.e("HubSpaceRepo", "Error getting team members for $teamName", e)
            TeamInfo(0, emptyList())
        }
    }

    suspend fun getUserTeams(email: String): List<String> {
        return try {
            val document = database.collection("whitelisted_users").document(email).get().await()

            if (!document.exists()) return listOf("Team Alpha")

            val teams = when (val rawTeams = document.get("teams")) {
                is List<*> -> rawTeams.filterIsInstance<String>()
                else -> null
            }

            if (teams.isNullOrEmpty()) listOf("Team Alpha") else teams
        } catch (e: Exception) {
            Log.e("HubSpaceRepo", "Error fetching user teams for $email", e)
            listOf("Team Alpha")
        }
    }

    suspend fun getAnnouncements(team: String): List<Map<String, Any>> {
        return try {
            val query =
                database.collection("announcements").whereEqualTo("team", team).get().await()
            query.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            Log.e("HubSpaceRepo", "Error fetching announcements", e)
            emptyList()
        }
    }

    suspend fun getEvents(team: String): List<Map<String, Any>> {
        return try {
            val query = database.collection("events").whereEqualTo("team", team).get().await()
            query.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            Log.e("HubSpaceRepo", "Error fetching events", e)
            emptyList()
        }
    }

    suspend fun getLatestAnnouncement(team: String): Map<String, Any>? {
        return try {
            val pinnedQuery = database.collection("announcements").whereEqualTo("pin", true)
                .whereEqualTo("team", team).get().await()

            if (!pinnedQuery.isEmpty) {
                return pinnedQuery.documents.first().data
            }

            val latestQuery = database.collection("announcements").whereEqualTo("team", team)
                .orderBy("at", Query.Direction.DESCENDING).limit(1).get().await()

            latestQuery.documents.firstOrNull()?.data
        } catch (e: Exception) {
            Log.e("HubSpaceRepo", "Error getting latest announcement", e)
            null
        }
    }

    suspend fun getLatestEvent(team: String): Map<String, Any>? {
        return try {
            val query = database.collection("events").let { q ->
                if (team != "All teams") q.whereEqualTo("team", team) else q
            }.orderBy("at", Query.Direction.ASCENDING).get().await()

            val currentTime = com.google.firebase.Timestamp(java.util.Date())
            Log.d("TEST", "Current Time: $currentTime")

            for (doc in query.documents) {
                val title = doc.getString("title")
                val eventTime = doc.getTimestamp("at")
                Log.d("TEST", "Event: $title | Time: $eventTime | Valid: ${eventTime != null && eventTime >= currentTime}")
            }

            val validDoc = query.documents.firstOrNull { doc ->
                val eventTime = doc.getTimestamp("at")
                eventTime != null && eventTime >= currentTime
            }

            validDoc?.data
        } catch (e: Exception) {
            Log.e("HubSpaceRepo", "Error getting latest event", e)
            null
        }
    }

    suspend fun getGithubUsername(teamName: String): String {
        return try {
            val snapshot = database.collection("teams").document(teamName).get().await()
            snapshot.getString("githubUsername") ?: "apw-android"
        } catch (e: Exception) {
            "apw-android"
        }
    }
}