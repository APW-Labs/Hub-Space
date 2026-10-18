package apw.android.hubspace.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun GitHubLiveCard(username: String, modifier: Modifier = Modifier) {
    var retry by remember { mutableIntStateOf(0) }

    val result by produceState<Result<GitHubStats>?>(initialValue = null, username, retry) {
        value = null
        value = loadGitHubStats(username)
    }

    val r = result
    val stats = r?.getOrNull()

    if (stats != null) {
        GitHubTeamProfileCard(
            modifier = modifier,
            username = username,
            totalContributions = NumberFormat.getInstance().format(stats.total),
            currentStreak = days(stats.currentStreak),
            longestStreak = days(stats.longestStreak),
            publicRepos = stats.repos,
            contributions = stats.heatmap
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                if (r == null) {
                    CircularProgressIndicator()
                } else {
                    Text("Couldn't load GitHub activity", style = MaterialTheme.typography.bodyMedium)
                    FilledTonalButton(onClick = { retry++ }) { Text("Try again") }
                }
            }
        }
    }
}

private fun days(n: Int) = if (n == 1) "1 day" else "$n days"

private class GitHubStats(
    val total: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val repos: Int,
    val heatmap: List<Int>
)

private suspend fun loadGitHubStats(user: String): Result<GitHubStats> = withContext(Dispatchers.IO) {
    try {
        val cal = JSONObject(get("https://github-contributions-api.jogruber.de/v4/$user?y=last"))
        val arr = cal.getJSONArray("contributions")
        val counts = List(arr.length()) { arr.getJSONObject(it).getInt("count") }
        val total = cal.getJSONObject("total").optInt("lastYear", counts.sum())

        val repos = JSONObject(get("https://api.github.com/users/$user")).getInt("public_repos")

        val (current, longest) = streaks(counts)
        Result.success(GitHubStats(total, current, longest, repos, counts.takeLast(26 * 7)))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}

private fun get(url: String): String {
    val c = URL(url).openConnection() as HttpURLConnection
    c.connectTimeout = 10_000
    c.readTimeout = 10_000
    return try {
        c.inputStream.bufferedReader().use { it.readText() }
    } finally {
        c.disconnect()
    }
}

private fun streaks(days: List<Int>): Pair<Int, Int> {
    var longest = 0
    var run = 0
    days.forEach { if (it > 0) { run++; if (run > longest) longest = run } else run = 0 }

    var i = days.lastIndex
    if (i >= 0 && days[i] == 0) i--
    var current = 0
    while (i >= 0 && days[i] > 0) { current++; i-- }
    return current to longest
}