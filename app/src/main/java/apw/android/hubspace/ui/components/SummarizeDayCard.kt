package apw.android.hubspace.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import apw.android.hubspace.ui.logos.campaign
import apw.android.hubspace.ui.logos.event
import apw.android.hubspace.ui.logos.timeline
import apw.android.hubspace.ui.theme.HubSpaceTheme
import kotlinx.coroutines.delay
import kotlin.math.min

sealed interface DaySummaryState {
    object Idle : DaySummaryState

    object Loading : DaySummaryState

    data class Success(
        val text: String,
        val highlights: List<SummaryHighlight> = emptyList(),
        val generatedAt: String = "Just now"
    ) : DaySummaryState

    data class Error(
        val message: String = "We couldn't generate your brief. Check your connection and try again."
    ) : DaySummaryState
}

enum class HighlightType { Tasks, Event, Notice }

data class SummaryHighlight(val type: HighlightType, val text: String)

@Composable
fun SummarizeDayCard(
    currentTeam: String,
    modifier: Modifier = Modifier,
    pendingTasksCount: Int = 0,
    hasUpcomingEvent: Boolean = false,
    eventTitle: String = "",
    latestAnnouncementTitle: String = "",
    summaryState: DaySummaryState = DaySummaryState.Idle,
    onSummarizeClick: () -> Unit = {},
    onRegenerateClick: () -> Unit = onSummarizeClick,
    onDismissClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val scheme = MaterialTheme.colorScheme
    val outerShape = RoundedCornerShape(24.dp)
    val loading = summaryState is DaySummaryState.Loading

    val angle = rotatingAngle(active = loading)
    val glow by animateFloatAsState(if (loading) 1f else 0f, tween(400), label = "glow")
    val restBorder by animateColorAsState(
        when (summaryState) {
            is DaySummaryState.Success -> scheme.primary.copy(alpha = 0.45f)
            is DaySummaryState.Error -> scheme.error.copy(alpha = 0.55f)
            else -> scheme.outlineVariant.copy(alpha = 0.6f)
        },
        tween(300),
        label = "border"
    )
    val aurora = listOf(scheme.primary, scheme.tertiary, scheme.secondary, scheme.primary)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(outerShape)
            .drawBehind {
                drawRect(restBorder)
                if (glow > 0f) {
                    val pad = size.maxDimension
                    rotate(angle.value) {
                        drawRect(
                            brush = Brush.sweepGradient(colors = aurora),
                            topLeft = Offset(-pad, -pad),
                            size = Size(size.width + pad * 2, size.height + pad * 2),
                            alpha = glow
                        )
                    }
                }
            }
            .padding(1.5.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(22.5.dp),
            color = scheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BriefHeader(
                    team = currentTeam.ifEmpty { "General Workspace" },
                    subtitle = when (summaryState) {
                        DaySummaryState.Idle -> "Get your AI brief for today"
                        DaySummaryState.Loading -> "Working on your brief…"
                        is DaySummaryState.Success -> "AI brief · ${summaryState.generatedAt}"
                        is DaySummaryState.Error -> "Something went wrong"
                    },
                    loading = loading
                )

                AnimatedContent(
                    targetState = summaryState,
                    contentKey = { it::class },
                    transitionSpec = {
                        (fadeIn(tween(240, delayMillis = 90)) togetherWith fadeOut(tween(90)))
                            .using(SizeTransform(clip = false))
                    },
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    label = "summary-body"
                ) { state ->
                    when (state) {
                        DaySummaryState.Idle -> IdleBody(onSummarizeClick)
                        DaySummaryState.Loading -> LoadingBody(
                            hasEvent = hasUpcomingEvent && eventTitle.isNotEmpty(),
                            hasNotice = latestAnnouncementTitle.isNotEmpty(),
                            onCancel = onCancelClick
                        )
                        is DaySummaryState.Success -> SuccessBody(state, onRegenerateClick, onDismissClick)
                        is DaySummaryState.Error -> ErrorBody(state.message, onRegenerateClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun BriefHeader(team: String, subtitle: String, loading: Boolean) {
    val scheme = MaterialTheme.colorScheme
    val pulse = pulseScale(active = loading)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = scheme.primaryContainer,
            modifier = Modifier
                .size(44.dp)
                .graphicsLayer {
                    scaleX = pulse.value
                    scaleY = pulse.value
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = AiIcons.Sparkle,
                    contentDescription = null,
                    tint = scheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = team,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = scheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun IdleBody(onSummarize: () -> Unit) {
    Button(
        onClick = onSummarize,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Icon(AiIcons.Sparkle, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("Summarize my day", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun FactRow(icon: ImageVector, label: String, text: String) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(shape = CircleShape, color = scheme.secondaryContainer, modifier = Modifier.size(26.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = scheme.onSecondaryContainer, modifier = Modifier.size(15.dp))
            }
        }
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = scheme.onSurface)) { append(label) }
                append("  ")
                append(text)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LoadingBody(hasEvent: Boolean, hasNotice: Boolean, onCancel: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val steps = remember(hasEvent, hasNotice) {
        buildList {
            add("Checking your tasks")
            if (hasNotice) add("Reading announcements")
            if (hasEvent) add("Looking at upcoming events")
            add("Writing your brief")
        }
    }
    var step by remember { mutableIntStateOf(0) }
    LaunchedEffect(steps) {
        while (true) {
            delay(1300)
            step = (step + 1) % steps.size
        }
    }
    val shimmer = shimmerBrush()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = scheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SkeletonLine(1f, shimmer)
                SkeletonLine(0.92f, shimmer)
                SkeletonLine(0.64f, shimmer)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = scheme.primary
            )
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    (slideInVertically { it / 2 } + fadeIn(tween(200))) togetherWith
                            (slideOutVertically { -it / 2 } + fadeOut(tween(150)))
                },
                modifier = Modifier.weight(1f),
                label = "status-step"
            ) { i ->
                Text(
                    text = steps[i.coerceIn(0, steps.lastIndex)],
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    }
}

@Composable
private fun SkeletonLine(fraction: Float, brush: Brush) {
    Box(
        modifier = Modifier
            .fillMaxWidth(fraction)
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(brush)
    )
}

@Composable
private fun SuccessBody(
    state: DaySummaryState.Success,
    onRegenerate: () -> Unit,
    onDismiss: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val clipboard = LocalClipboardManager.current

    var played by rememberSaveable(state.text) { mutableStateOf(false) }
    var shown by remember(state.text) { mutableIntStateOf(if (played) state.text.length else 0) }
    LaunchedEffect(state.text) {
        if (played) {
            shown = state.text.length
        } else {
            while (shown < state.text.length) {
                shown = min(shown + 2, state.text.length)
                delay(16)
            }
            played = true
        }
    }
    val done = shown >= state.text.length

    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(1600)
            copied = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = scheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = state.text.take(shown),
                    style = MaterialTheme.typography.bodyLarge,
                    color = scheme.onSurface
                )
                AnimatedVisibility(
                    visible = done && state.highlights.isNotEmpty(),
                    enter = fadeIn(tween(300)) + expandVertically(tween(300))
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HorizontalDivider(color = scheme.outlineVariant.copy(alpha = 0.5f))
                        state.highlights.forEach { h ->
                            val (icon, label) = when (h.type) {
                                HighlightType.Tasks -> timeline to "Tasks"
                                HighlightType.Event -> event to "Next"
                                HighlightType.Notice -> campaign to "Notice"
                            }
                            FactRow(icon, label, h.text)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AI-generated. May contain mistakes.",
                style = MaterialTheme.typography.labelSmall,
                color = scheme.onSurfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
            val buttonColors = IconButtonDefaults.iconButtonColors(contentColor = scheme.onSurfaceVariant)
            IconButton(
                onClick = {
                    clipboard.setText(AnnotatedString(buildString {
                        append(state.text)
                        state.highlights.forEach { append("\n• ${it.text}") }
                    }))
                    copied = true
                },
                colors = buttonColors,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (copied) AiIcons.Check else AiIcons.Copy,
                    contentDescription = if (copied) "Copied" else "Copy summary",
                    tint = if (copied) scheme.primary else scheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = onRegenerate, colors = buttonColors, modifier = Modifier.size(40.dp)) {
                Icon(AiIcons.Refresh, "Regenerate summary", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onDismiss, colors = buttonColors, modifier = Modifier.size(40.dp)) {
                Icon(AiIcons.Close, "Dismiss summary", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ErrorBody(message: String, onRetry: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = scheme.errorContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = AiIcons.Warning,
                    contentDescription = null,
                    tint = scheme.onErrorContainer,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onErrorContainer
                )
            }
        }
        FilledTonalButton(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(AiIcons.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Try again", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun rotatingAngle(active: Boolean): State<Float> {
    if (!active) return remember { mutableFloatStateOf(0f) }
    val transition = rememberInfiniteTransition(label = "aurora")
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
        label = "angle"
    )
}

@Composable
private fun pulseScale(active: Boolean): State<Float> {
    if (!active) return remember { mutableFloatStateOf(1f) }
    val transition = rememberInfiniteTransition(label = "pulse")
    return transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )
}

@Composable
private fun shimmerBrush(): Brush {
    val scheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1300, easing = LinearEasing)),
        label = "x"
    )
    val base = scheme.surfaceContainerHighest
    val highlight = scheme.surfaceContainerHigh.copy(alpha = 0.4f)
    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(x * 600f, 0f),
        end = Offset(x * 600f + 400f, 0f)
    )
}

private object AiIcons {
    val Sparkle = icon(
        "Sparkle",
        "M19 9l1.25-2.75L23 5l-2.75-1.25L19 1l-1.25 2.75L15 5l2.75 1.25L19 9zm-7.5.5L9 4 6.5 9.5 1 12l5.5 2.5L9 20l2.5-5.5L17 12l-5.5-2.5zM19 15l-1.25 2.75L15 19l2.75 1.25L19 23l1.25-2.75L23 19l-2.75-1.25L19 15z"
    )
    val Refresh = icon(
        "Refresh",
        "M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"
    )
    val Copy = icon(
        "Copy",
        "M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"
    )
    val Check = icon("Check", "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z")
    val Close = icon(
        "Close",
        "M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"
    )
    val Warning = icon("Warning", "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z")

    private fun icon(name: String, path: String): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(pathData = addPathNodes(path), fill = SolidColor(Color.Black)).build()
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun SummarizeDayCardStatesPreview() {
    HubSpaceTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            val common = Modifier
            SummarizeDayCard(
                currentTeam = "Android Team", modifier = common,
                pendingTasksCount = 4, hasUpcomingEvent = true,
                eventTitle = "Sprint review", latestAnnouncementTitle = "Release freeze on Friday",
                summaryState = DaySummaryState.Idle
            )
            SummarizeDayCard(
                currentTeam = "Android Team", modifier = common,
                pendingTasksCount = 4, summaryState = DaySummaryState.Loading
            )
            SummarizeDayCard(
                currentTeam = "Android Team", modifier = common,
                summaryState = DaySummaryState.Success(
                    text = "You have 4 active tasks and a sprint review coming up. Wrap the PR reviews before the release freeze on Friday.",
                    highlights = listOf(
                        SummaryHighlight(HighlightType.Tasks, "4 active in your team's queue"),
                        SummaryHighlight(HighlightType.Event, "Sprint review"),
                        SummaryHighlight(HighlightType.Notice, "Release freeze on Friday")
                    )
                )
            )
            SummarizeDayCard(
                currentTeam = "Android Team", modifier = common,
                summaryState = DaySummaryState.Error()
            )
        }
    }
}