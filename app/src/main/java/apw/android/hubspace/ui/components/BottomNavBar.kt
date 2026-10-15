package apw.android.hubspace.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import apw.android.hubspace.ui.logos.dynamic_feed
import apw.android.hubspace.ui.logos.home
import apw.android.hubspace.ui.logos.notes
import apw.android.hubspace.ui.logos.reminder
import apw.android.hubspace.ui.logos.shield_lock
import apw.android.hubspace.ui.logos.timeline

sealed class BottomNavTab(
    val route: String,
    val title: String,
    val iconName: String
) {
    object Home: BottomNavTab("home", "Home", "home")
    object Announcements: BottomNavTab("announcements", "Feed", "feed")
    object Timeline: BottomNavTab("timeline", "Timeline", "timeline")
    object Reminders: BottomNavTab("reminders", "Reminders", "alarm")
    object Notes: BottomNavTab("notes", "Notes", "notes")
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        BottomNavTab.Home,
        BottomNavTab.Announcements,
        BottomNavTab.Timeline,
        BottomNavTab.Reminders,
        BottomNavTab.Notes
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route

            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab.route) },
                icon = {
                    Icon(
                        imageVector = when (tab.iconName) {
                            "feed" -> dynamic_feed
                            "timeline" -> timeline
                            "alarm" -> reminder
                            "notes" -> notes
                            else -> home
                        },
                        contentDescription = tab.title
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}