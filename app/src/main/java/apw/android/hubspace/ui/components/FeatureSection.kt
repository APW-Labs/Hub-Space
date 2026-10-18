package apw.android.hubspace.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apw.android.hubspace.R
import apw.android.hubspace.ui.fonts.LoginHead
import apw.android.hubspace.ui.logos.dynamic_feed
import apw.android.hubspace.ui.logos.sync
import apw.android.hubspace.ui.logos.timeline

data class FeatureItem(
    val title: String,
    val description: String,
    val iconRes: ImageVector
)

@Composable
fun ValueCard(
    title: String,
    description: String,
    iconRes: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconRes,
                    contentDescription = title,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = LoginHead,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = LoginHead,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun FeatureSection(modifier: Modifier = Modifier) {
    val features = listOf(
        FeatureItem(
            title = "Broadcasts & Updates",
            description = "Stay aligned with pinned announcements from team leads.",
            iconRes = dynamic_feed
        ),
        FeatureItem(
            title = "Shared Timelines",
            description = "Never miss hackathon milestones or event deadlines.",
            iconRes = timeline
        ),
        FeatureItem(
            title = "Task & Deadline Sync",
            description = "Keep track of team deliverables in real-time.",
            iconRes = sync
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        features.forEach { feature ->
            ValueCard(
                title = feature.title,
                description = feature.description,
                iconRes = feature.iconRes
            )
        }
    }
}
