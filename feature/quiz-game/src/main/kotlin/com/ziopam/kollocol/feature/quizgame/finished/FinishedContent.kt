package com.ziopam.kollocol.feature.quizgame.finished

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.cards.LargeBottomCard
import com.ziopam.kollocol.core.ui.contentPadding
import com.ziopam.kollocol.core.ui.preview.AppPreview
import com.ziopam.kollocol.domain.model.GameFinishResult
import com.ziopam.kollocol.feature.quizgame.GameHeader
import com.ziopam.kollocol.feature.quizgame.GamePhase
import com.ziopam.kollocol.feature.quizgame.R
import com.ziopam.kollocol.feature.quizgame.preview.GamePreviewData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
internal fun FinishedContent(
    finished: GamePhase.Finished,
    quizName: String,
    onNavigateBack: () -> Unit
) {
    val sorted = finished.leaderboard.sortedBy { it.rank }
    val first  = sorted.firstOrNull { it.rank == 1 }
    val second = sorted.firstOrNull { it.rank == 2 }
    val third  = sorted.firstOrNull { it.rank == 3 }
    val rest   = sorted.filter { it.rank > 3 }

    val density = LocalDensity.current
    val heroInitialOffsetPx = with(density) { 72.dp.toPx() }

    val titleAlpha      = remember { Animatable(0f) }
    val titleOffsetY    = remember { Animatable(0f) }
    val headerAlpha     = remember { Animatable(0f) }
    val cardAlpha       = remember { Animatable(0f) }

    val heroAlpha       = remember { Animatable(0f) }
    val heroSlideY      = remember { Animatable(heroInitialOffsetPx) }

    val secondOffsetX   = remember { Animatable(250f) }
    val secondAlpha     = remember { Animatable(0f) }
    val thirdOffsetX    = remember { Animatable(-250f) }
    val thirdAlpha      = remember { Animatable(0f) }

    val yourResultAlpha = remember { Animatable(0f) }
    val othersAlpha     = remember { Animatable(0f) }
    val restAlphas      = remember { List(rest.size) { Animatable(0f) } }

    var showConfetti by remember { mutableStateOf(false) }

    var rootSize          by remember { mutableStateOf(IntSize.Zero) }
    var headerTitleCenter by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(Unit) {
        snapshotFlow { rootSize.width > 0 && headerTitleCenter != Offset.Zero }.first { it }

        val centerY = rootSize.height / 2f

        titleAlpha.animateTo(1f, tween(600, easing = EaseOutCubic))
        delay(500)

        titleOffsetY.animateTo(headerTitleCenter.y - centerY, tween(700, easing = EaseInOutCubic))
        launch { titleAlpha.animateTo(0f, tween(250)) }
        headerAlpha.animateTo(1f, tween(250))
        delay(300)

        showConfetti = true
        if (first != null) {
            launch { heroAlpha.animateTo(1f, tween(500, easing = EaseOutCubic)) }
            heroSlideY.animateTo(0f, tween(600, easing = EaseOutBack))
            delay(800)
            heroAlpha.animateTo(0f, tween(300))
        }

        cardAlpha.animateTo(1f, tween(500))

        launch { secondOffsetX.animateTo(0f, tween(500, easing = EaseOutBack)) }
        launch { secondAlpha.animateTo(1f, tween(450)) }
        delay(150)
        launch { thirdOffsetX.animateTo(0f, tween(500, easing = EaseOutBack)) }
        launch { thirdAlpha.animateTo(1f, tween(450)) }
        delay(300)

        yourResultAlpha.animateTo(1f, tween(500))

        othersAlpha.animateTo(1f, tween(500))
        restAlphas.forEachIndexed { i, anim ->
            launch {
                delay((i * 300).toLong())
                anim.animateTo(1f, tween(500, easing = EaseOutCubic))
            }
        }
    }

    val topPad = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { rootSize = it.size }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = topPad)
                    .padding(contentPadding)
                    .onGloballyPositioned { coords ->
                        val pos = coords.positionInRoot()
                        headerTitleCenter = Offset(
                            pos.x + coords.size.width / 2f,
                            pos.y + coords.size.height / 2f
                        )
                    }
            ) {
                Box(modifier = Modifier.alpha(headerAlpha.value)) {
                    GameHeader(quizName = quizName, onExit = onNavigateBack)
                }
            }

            LargeBottomCard(
                modifier = Modifier
                    .weight(1f)
                    .alpha(cardAlpha.value),
                scrollable = false
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = stringResource(R.string.game_leaderboard),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(16.dp))

                        PodiumSection(
                            first = first,
                            second = second,
                            third = third,
                            secondOffsetX = secondOffsetX.value,
                            secondAlpha = secondAlpha.value,
                            thirdOffsetX = thirdOffsetX.value,
                            thirdAlpha = thirdAlpha.value
                        )

                        finished.result?.let { result ->
                            if (finished.result.rank != 0) {
                                Column(modifier = Modifier.alpha(yourResultAlpha.value)) {
                                    Spacer(Modifier.height(12.dp))
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    YourResultRow(result = result)
                                }
                            }
                        }

                        if (rest.isNotEmpty()) {
                            Column(modifier = Modifier.alpha(othersAlpha.value)) {
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                                Spacer(Modifier.height(12.dp))
                                OtherParticipantsSection(
                                    participants = rest,
                                    sectionAlpha = 1f,
                                    itemAlphas = restAlphas
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }

                    Spacer(Modifier.height(8.dp))
                    DefaultButton(
                        text = stringResource(R.string.game_exit),
                        onClick = onNavigateBack,
                        isButtonEnabled = true,
                        isWidthLimited = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        ConfettiOverlay(visible = showConfetti)

        if (titleAlpha.value > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = titleOffsetY.value
                        alpha = titleAlpha.value
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quizName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }

        if (first != null && heroAlpha.value > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = heroSlideY.value
                        alpha = heroAlpha.value
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "\uD83E\uDD47", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    AvatarPicker(
                        avatarUrl = first.avatarUrl,
                        onClick = {},
                        modifier = Modifier.size(100.dp),
                        defaultIconSize = 56.dp,
                        borderColor = Color(0xFFFFD700)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = first.name.ifBlank { first.userId },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${first.score}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun FinishedContentPreview() {
    AppPreview {
        FinishedContent(
            finished = GamePhase.Finished(
                result = GameFinishResult(1500, 3),
                leaderboard = GamePreviewData.leaderboardEntries
            ),
            quizName = "Коллоквиум iOS",
            onNavigateBack = {}
        )
    }
}
