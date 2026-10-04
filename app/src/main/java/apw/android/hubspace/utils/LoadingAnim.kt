package apw.android.hubspace.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import apw.android.hubspace.ui.theme.HubSpaceTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val MAX_MESSAGES = 5
private const val INTRO_MILLIS = 1_200L

private val DefaultMessages = listOf(
    "Syncing your teams…",
    "Fetching announcements…",
    "Loading your timeline…",
    "Setting up event alarms…",
    "Almost there…"
)

@Composable
fun LoadingGate(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    messages: List<String> = DefaultMessages,
    content: @Composable () -> Unit
) {
    var animationFinished by rememberSaveable { mutableStateOf(false) }
    val showLoading = isLoading || !animationFinished

    Crossfade(
        targetState = showLoading,
        modifier = modifier,
        animationSpec = tween(durationMillis = 500),
        label = "loading-gate"
    ) { loading ->
        if (loading) {
            LoadingScreen(
                messages = messages,
                onAnimationFinished = { animationFinished = true }
            )
        } else {
            content()
        }
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    messages: List<String> = DefaultMessages,
    messageDurationMillis: Long = 900L,
    onAnimationFinished: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    val shown = remember(messages) { messages.take(MAX_MESSAGES).ifEmpty { listOf("Loading…") } }
    val currentOnFinished by rememberUpdatedState(onAnimationFinished)

    var messageIndex by remember { mutableIntStateOf(0) }
    var showText by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(700.milliseconds)
        showText = true
    }

    LaunchedEffect(shown) {
        shown.indices.forEach { index ->
            messageIndex = index
            delay((if (index == 0) maxOf(messageDurationMillis, INTRO_MILLIS) else messageDurationMillis).milliseconds)
        }
        currentOnFinished()
    }

    val textReveal by animateFloatAsState(
        targetValue = if (showText) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "text-reveal"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface)
            .semantics(mergeDescendants = true) { contentDescription = "Loading HubSpace" }
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HubLogoAnimation()

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier.graphicsLayer {
                alpha = textReveal
                translationY = (1f - textReveal) * 24.dp.toPx()
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HubSpace",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Announce · Schedule · Remember",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(48.dp))

        LoadingDots()

        Spacer(Modifier.height(16.dp))

        AnimatedContent(
            targetState = shown[messageIndex.coerceIn(shown.indices)],
            transitionSpec = {
                (fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 3 }) togetherWith
                        fadeOut(tween(200))
            },
            label = "loading-message"
        ) { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HubLogoAnimation(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val primary = colors.primary
    val onPrimary = colors.onPrimary
    val tertiary = colors.tertiary
    val container = colors.primaryContainer

    val lineProgress = remember { Animatable(0f) }
    val nodeScales = remember { List(3) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        launch {
            lineProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 900, delayMillis = 200, easing = FastOutSlowInEasing)
            )
        }
        nodeScales.forEachIndexed { index, scale ->
            launch {
                delay((250L + index * 180L).milliseconds)
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }

    val infinite = rememberInfiniteTransition(label = "logo")
    val ripple by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2_200, easing = LinearEasing)),
        label = "ripple"
    )
    val breathe by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    Canvas(modifier = modifier.size(180.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val nodePositions = listOf(
            Offset(size.width * 0.18f, size.height * 0.20f),
            Offset(size.width * 0.84f, size.height * 0.30f),
            Offset(size.width * 0.42f, size.height * 0.88f)
        )

        drawCircle(color = container.copy(alpha = 0.45f), radius = size.minDimension / 2f, center = center)

        repeat(2) { i ->
            val t = (ripple + i * 0.5f) % 1f
            drawCircle(
                color = (if (i == 0) primary else tertiary).copy(alpha = (1f - t) * 0.35f),
                radius = 34.dp.toPx() + t * 60.dp.toPx(),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        nodePositions.forEach { target ->
            val end = center + (target - center) * lineProgress.value
            drawLine(
                color = primary.copy(alpha = 0.45f),
                start = center,
                end = end,
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        nodePositions.forEachIndexed { index, position ->
            val radius = (17.dp.toPx() * nodeScales[index].value).coerceAtLeast(0f)
            drawCircle(color = container, radius = radius, center = position)
            drawCircle(color = primary, radius = radius, center = position, style = Stroke(width = 2.dp.toPx()))
        }

        drawCircle(color = primary, radius = 30.dp.toPx() * breathe, center = center)
        drawCircle(color = onPrimary, radius = 10.dp.toPx() * breathe, center = center)
    }
}

@Composable
private fun LoadingDots(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "dots")
    val dotColor = MaterialTheme.colorScheme.primary

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            val progress by infinite.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 160)
                ),
                label = "dot-$index"
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .graphicsLayer {
                        scaleX = progress
                        scaleY = progress
                        alpha = progress
                    }
                    .background(dotColor, CircleShape)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LoadingScreenPreview() {
    HubSpaceTheme (dynamicColor = true) {
        LoadingScreen()
    }
}