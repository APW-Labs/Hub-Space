package apw.android.hubspace.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import apw.android.hubspace.backend.data.FeedItem
import apw.android.hubspace.backend.data.FeedType
import apw.android.hubspace.ui.components.AnnouncementItem
import apw.android.hubspace.ui.components.BottomNavBar
import apw.android.hubspace.ui.components.EventItem
import apw.android.hubspace.ui.components.FeedCard
import apw.android.hubspace.ui.components.GitHubLiveCard
import apw.android.hubspace.ui.components.QuickStatsRow
import apw.android.hubspace.ui.components.SmartHeroBanner
import apw.android.hubspace.ui.components.SummarizeDayCard
import apw.android.hubspace.ui.components.TeamSelectionSheet
import apw.android.hubspace.ui.components.TopBar
import apw.android.hubspace.ui.theme.HubSpaceTheme
import apw.android.hubspace.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    email: String, onTabSelected: (String) -> Unit = {},
    viewModel: AuthViewModel = viewModel(),
    onSignOutClicked: () -> Unit
) {
    val currentTeam = viewModel.userTeam
    var currentRoute by remember { mutableStateOf("home") }
    var showTeamSheet by remember { mutableStateOf(false) }
    val teamsList = viewModel.teamsList
    val latestAnnouncement = viewModel.latestAnnouncement
    val latestEvent = viewModel.latestEvent
    val membersCount = viewModel.memberCount

    Scaffold(
        topBar = {
            TopBar(
                currentTeam = currentTeam,
                unreadNotificationsCount = 1,
                userInitial = if (email.isNotEmpty()) email.uppercase()[0] else 'H',
                onMenuClick = { },
                onProfileClick = { onSignOutClicked() },
                onTeamSelectClick = { showTeamSheet = true },
                onNotificationClick = {}
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute, onTabSelected = { route ->
                    currentRoute = route
                    onTabSelected(route)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                "home" -> {
                    HomeScreenContent(
                        currentTeam = currentTeam,
                        email = email,
                        membersCount = membersCount,
                        latestAnnouncement = latestAnnouncement,
                        latestEvent = latestEvent,
                        viewModel = viewModel
                    )
                }

                "announcements" -> {
                    FeedScreenContent(currentTeam = currentTeam)
                }

                "timeline" -> {
                    TasksScreenContent()
                }

                "reminders" -> {
                    RemindersScreenContent()
                }

                "notes" -> {
                    NotesScreenContent()
                }
            }
        }
    }

    if (showTeamSheet) {
        TeamSelectionSheet(
            currentTeam = currentTeam,
            teams = teamsList,
            onTeamSelected = { selected ->
                viewModel.switchTeam(selected)
                showTeamSheet = false
            },
            onDismiss = { showTeamSheet = false })
    }
}

@Composable
fun HomeScreenContent(
    currentTeam: String,
    email: String,
    membersCount: Int,
    latestAnnouncement: AnnouncementItem?,
    latestEvent: EventItem?,
    viewModel: AuthViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp
        ), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SmartHeroBanner(
                currentTeam = currentTeam,
                userName = email.substringBefore("@"),
                pinnedAnnouncement = latestAnnouncement,
                upcomingEvent = latestEvent
            )
        }
        item {
            QuickStatsRow(
                activeTasksCount = 4, teamMembersCount = membersCount, deadlinesCount = 1
            )
        }
        item {
            GitHubLiveCard(
                username = viewModel.teamGithubUserName
            )
        }
        item {
            SummarizeDayCard(
                currentTeam = currentTeam,
                pendingTasksCount = 4,
                hasUpcomingEvent = latestEvent != null,
                eventTitle = latestEvent?.title ?: "",
                latestAnnouncementTitle = latestAnnouncement?.title ?: ""
            )
        }
    }
}

@Composable
fun FeedScreenContent(
    currentTeam: String,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Announcement", "Event", "Activity")

    val feedList = remember {
        listOf(
            FeedItem(
                id = "1",
                title = "Final submission closes Sunday 6 PM",
                body = "Devpost submission closes Sunday at 6 PM IST. Every team needs a public repo and 3-minute demo video.",
                postedBy = "Shagun",
                timeStamp = "2 hours ago",
                type = FeedType.ANNOUNCEMENT,
                isPinned = true
            ),
            FeedItem(
                id = "2",
                title = "Internal Code Review Sync",
                body = "Joined main branch sync for HubSpace Jetpack Compose architecture review with core contributors.",
                postedBy = "Shabd Vasudeva",
                timeStamp = "5 hours ago",
                type = FeedType.EVENT
            ),
            FeedItem(
                id = "3",
                title = "GitHub Actions Pipeline Status",
                body = "Automated APK build pipeline passed successfully for release candidate v1.0.2.",
                postedBy = "System",
                timeStamp = "Yesterday",
                type = FeedType.ACTIVITY
            )
        )
    }

    val filteredList = remember(selectedFilter, feedList) {
        when (selectedFilter) {
            "Announcement" -> feedList.filter { it.type == FeedType.ANNOUNCEMENT }
            "Event" -> feedList.filter { it.type == FeedType.EVENT }
            "Activity" -> feedList.filter { it.type == FeedType.ACTIVITY }
            else -> feedList
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Team Feed",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Live updates from $currentTeam",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredList, key = { it.id }) { item ->
                FeedCard(feedItem = item)
            }
        }
    }
}

@Composable
fun TasksScreenContent() {
    Text(
        modifier = Modifier.fillMaxSize(), text = "Task", textAlign = TextAlign.Center
    )
}

@Composable
fun RemindersScreenContent() {
    Text(
        modifier = Modifier.fillMaxSize(), text = "Reminders", textAlign = TextAlign.Center
    )
}

@Composable
fun NotesScreenContent() {
    Text(
        modifier = Modifier.fillMaxSize(), text = "Notes", textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewMain() {
    HubSpaceTheme {
        MainScreen(
            email = "shabdvasudeva@gmail.com",
            onSignOutClicked = { }
        )
    }
}