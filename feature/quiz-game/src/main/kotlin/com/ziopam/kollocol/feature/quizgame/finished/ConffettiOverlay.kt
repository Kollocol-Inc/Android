package com.ziopam.kollocol.feature.quizgame.finished

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import java.util.Random
import kotlin.math.sin

@Composable
internal fun ConfettiOverlay(visible: Boolean) {
    val particles = rememberConfettiParticles()
    val progress = remember { Animatable(0f) }
    val overlayAlpha = remember { Animatable(0f) }

    LaunchedEffect(visible) {
        if (visible) {
            overlayAlpha.snapTo(1f)
            // Play one full fall cycle, then fade out
            progress.animateTo(1f, tween(3000, easing = LinearEasing))
            overlayAlpha.animateTo(0f, tween(600))
        }
    }

    if (overlayAlpha.value > 0.01f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(overlayAlpha.value)
        ) {
            particles.forEach { p ->
                val t = progress.value * p.speedFactor
                val yFrac = p.startY + t * 1.4f
                val xSwing = p.swingAmplitude * sin(
                    (t * p.swingFrequency * 2.0 * Math.PI).toFloat()
                )
                val rotation = t * p.rotationSpeed
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationX = p.startX * size.width + xSwing
                            translationY = yFrac * size.height
                            rotationZ = rotation
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(p.size.dp)
                            .clip(if (p.size > 10f) RoundedCornerShape(2.dp) else CircleShape)
                            .background(p.color)
                    )
                }
            }
        }
    }
}

private data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val color: Color,
    val size: Float,
    val speedFactor: Float,
    val swingAmplitude: Float,
    val swingFrequency: Float,
    val rotationSpeed: Float
)

private val confettiColors = listOf(
    Color(0xFFE53935), Color(0xFFFFB300), Color(0xFF43A047),
    Color(0xFF1E88E5), Color(0xFFAB47BC), Color(0xFFFF7043),
    Color(0xFF00ACC1), Color(0xFFEC407A)
)

@Composable
private fun rememberConfettiParticles(count: Int = 60): List<ConfettiParticle> = remember {
    val rng = Random(42)
    List(count) {
        ConfettiParticle(
            startX = rng.nextFloat(),
            startY = -(rng.nextFloat() * 0.4f),
            color = confettiColors[rng.nextInt(confettiColors.size)],
            size = 6f + rng.nextFloat() * 8f,
            speedFactor = 0.5f + rng.nextFloat() * 0.9f,
            swingAmplitude = 20f + rng.nextFloat() * 40f,
            swingFrequency = 0.5f + rng.nextFloat() * 1.5f,
            rotationSpeed = (if (rng.nextBoolean()) 1f else -1f) * (120f + rng.nextFloat() * 240f)
        )
    }
}
