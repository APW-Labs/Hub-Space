package apw.android.hubspace.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import apw.android.hubspace.ui.logos.healing
import apw.android.hubspace.ui.logos.timeline
import kotlin.random.Random

@Composable
fun GitHubTeamProfileCard(
    modifier: Modifier = Modifier,
    username: String = "apw-android",
    totalContributions: String = "1,428",
    longestStreak: String = "42 days",
    currentStreak: String = "12 days",
    publicRepos: Int = 18,
    contributions: List<Int> = SampleContributions,
    heatmapVisible: Boolean = true
) {
    val scheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = scheme.surfaceContainer),
        border = BorderStroke(1.dp, scheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = scheme.primaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = timeline,
                            contentDescription = null,
                            tint = scheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "@$username",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = scheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "GitHub contributions",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = scheme.tertiaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(start = 10.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = healing,
                            contentDescription = null,
                            tint = scheme.onTertiaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$currentStreak streak",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = scheme.onTertiaryContainer,
                            maxLines = 1
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = totalContributions,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = scheme.onSurface
                )
                Text(
                    text = "contributions in the last year",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = scheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ContributionHeatmap(counts = contributions, visible = heatmapVisible)
                    HeatmapLegend()
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileStatTile(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    label = "Current streak",
                    value = currentStreak,
                    container = scheme.primaryContainer,
                    content = scheme.onPrimaryContainer
                )
                ProfileStatTile(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    label = "Longest streak",
                    value = longestStreak,
                    container = scheme.secondaryContainer,
                    content = scheme.onSecondaryContainer
                )
                ProfileStatTile(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    label = "Public repos",
                    value = "$publicRepos repos",
                    container = scheme.tertiaryContainer,
                    content = scheme.onTertiaryContainer
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(scheme.primary, CircleShape)
                )
                Text(
                    text = "Active. Consistent commits across Android modules.",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant
                )
            }
        }
    }
}

private const val DAYS_PER_WEEK = 7

private fun levelFor(count: Int): Int = when {
    count <= 0 -> 0
    count <= 2 -> 1
    count <= 5 -> 2
    count <= 9 -> 3
    else -> 4
}

private val LevelAlpha = floatArrayOf(0f, 0.30f, 0.55f, 0.80f, 1f)

@Composable
private fun ContributionHeatmap(
    counts: List<Int>,
    visible: Boolean = true,
    modifier: Modifier = Modifier
) {
    val levels = remember(counts) { counts.map(::levelFor) }
    val cols = (levels.size / DAYS_PER_WEEK).coerceAtLeast(1)
    val gap = 3.dp
    val accent = MaterialTheme.colorScheme.primary
    val empty = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)

    val reveal = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (visible) {
            reveal.snapTo(0f)
            reveal.animateTo(1f, tween(durationMillis = 1100, easing = FastOutSlowInEasing))
        }
    }

    val total = counts.sum()
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val cell = (maxWidth - gap * (cols - 1)) / cols
        val gridHeight = cell * DAYS_PER_WEEK + gap * (DAYS_PER_WEEK - 1)

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(gridHeight)
                .semantics { contentDescription = "Contribution graph, $total contributions over $cols weeks" }
        ) {
            val g = gap.toPx()
            val c = (size.width - g * (cols - 1)) / cols
            val radius = CornerRadius(c * 0.28f)
            val progress = reveal.value * (cols + 6)

            for (col in 0 until cols) {
                val fade = (progress - col).coerceIn(0f, 1f)
                if (fade <= 0f) continue
                for (row in 0 until DAYS_PER_WEEK) {
                    val level = levels.getOrElse(col * DAYS_PER_WEEK + row) { 0 }
                    val color: Color =
                        if (level == 0) empty.copy(alpha = empty.alpha * fade)
                        else accent.copy(alpha = LevelAlpha[level] * fade)
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(col * (c + g), row * (c + g)),
                        size = Size(c, c),
                        cornerRadius = radius
                    )
                }
            }
        }
    }
}

@Composable
private fun HeatmapLegend() {
    val accent = MaterialTheme.colorScheme.primary
    val empty = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Last ${SampleWeeks} weeks",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Less",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            for (level in 0..4) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = if (level == 0) empty else accent.copy(alpha = LevelAlpha[level]),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
            Text(
                text = "More",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileStatTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    container: Color,
    content: Color
) {
    val parts = value.split(" ", limit = 2)
    val number = parts[0]
    val unit = parts.getOrNull(1)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = container,
        contentColor = content
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = content.copy(alpha = 0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1
                )
                if (unit != null) {
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelMedium,
                        color = content.copy(alpha = 0.75f),
                        modifier = Modifier.padding(bottom = 4.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private const val SampleWeeks = 26

private val SampleContributions: List<Int> = Random(42).let { rnd ->
    List(SampleWeeks * DAYS_PER_WEEK) { i ->
        val weekday = i % DAYS_PER_WEEK
        val quiet = weekday == 0 || weekday == 6          // lighter weekends
        val roll = rnd.nextInt(100)
        when {
            quiet && roll < 55 -> 0
            roll < 15 -> 0
            roll < 50 -> rnd.nextInt(1, 3)
            roll < 80 -> rnd.nextInt(3, 6)
            roll < 94 -> rnd.nextInt(6, 10)
            else -> rnd.nextInt(10, 16)
        }
    }
}