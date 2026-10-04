package apw.android.hubspace.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apw.android.hubspace.ui.components.AuthBottomSection
import apw.android.hubspace.ui.components.FeatureSection
import apw.android.hubspace.ui.components.TagChip
import apw.android.hubspace.ui.fonts.LoginHead
import apw.android.hubspace.ui.logos.HubSpaceLogo
import apw.android.hubspace.ui.theme.HubSpaceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onGoogleSignInClick: () -> Unit,
    isLoading: Boolean
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(start = 20.dp, end = 20.dp, top = 15.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HubSpaceLogo(
                        containerSize = 52.dp,
                        iconSize = 32.dp
                    )
                    Text(
                        text = "HubSpace",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LoginHead,
                        letterSpacing = (-0.2).sp,
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Your team's central workspace",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LoginHead,
                        letterSpacing = (-0.2).sp,
                        lineHeight = 36.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        minLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Manage schedules, lead announcements, notes, and deadlines in one central spot. Seamlessly collaborate with your team in real time.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = LoginHead,
                        lineHeight = 21.sp,
                        letterSpacing = 0.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TagChip(text = "Hackathons")
                    TagChip(text = "Project Teams")
                    TagChip(text = "Events")
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            FeatureSection()
            Spacer(modifier = Modifier.weight(1f))

            AuthBottomSection(
                onGoogleSignInClick = onGoogleSignInClick,
                isLoading = isLoading
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewWelcome() {
    HubSpaceTheme(darkTheme = true) {
        WelcomeScreen(
            onGoogleSignInClick = {},
            isLoading = true,
        )
    }
}