package apw.android.hubspace.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import apw.android.hubspace.ui.components.BottomNavBar
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
    email: String,
    viewModel: AuthViewModel = viewModel(),
    onSignOutClicked: () -> Unit
) {
    val currentTeam = viewModel.userTeam
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
                currentRoute = "home",
                onTabSelected = { route -> }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            SmartHeroBanner(
                currentTeam = currentTeam,
                userName = email.substringBefore("@"),
                pinnedAnnouncement = latestAnnouncement,
                upcomingEvent = latestEvent,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            QuickStatsRow(
                activeTasksCount = 4,
                teamMembersCount = membersCount,
                deadlinesCount = 1
            )
            Spacer(modifier = Modifier.height(14.dp))
            SummarizeDayCard(
                currentTeam = currentTeam,
                pendingTasksCount = 4,
                hasUpcomingEvent = latestEvent != null,
                eventTitle = latestEvent?.title ?: "",
                latestAnnouncementTitle = latestAnnouncement?.title ?: ""
            )
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
            onDismiss = { showTeamSheet = false }
        )
    }
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