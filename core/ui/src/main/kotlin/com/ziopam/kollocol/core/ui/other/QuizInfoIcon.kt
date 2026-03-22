package com.ziopam.kollocol.core.ui.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ziopam.kollocol.core.ui.R
import com.ziopam.kollocol.core.ui.clickableNoIndication
import com.ziopam.kollocol.domain.model.QuizMode

@Composable
fun QuizInfoIcon(
    quizMode: QuizMode,
    size: Dp
) {
    val visibility = remember { MutableTransitionState(false) }

    Box {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.info),
            contentDescription = stringResource(R.string.quiz_mode_info),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            modifier = Modifier
                .size(size)
                .clickableNoIndication{ visibility.targetState = !visibility.targetState }
        )

        if (visibility.currentState || visibility.targetState) {
            Popup(
                alignment = Alignment.BottomCenter,
                onDismissRequest = { visibility.targetState = false },
                properties = PopupProperties(focusable = true)
            ) {
                AnimatedVisibility(
                    visibleState = visibility,
                    enter =
                        fadeIn(spring(stiffness = Spring.StiffnessMedium)) +
                                slideInVertically(
                                    initialOffsetY = { it / 3 },
                                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                                ) +
                                scaleIn(
                                    initialScale = 0.96f,
                                    transformOrigin = TransformOrigin(0.5f, 1f),
                                    animationSpec = spring(
                                        stiffness = Spring.StiffnessMedium,
                                        dampingRatio = Spring.DampingRatioNoBouncy
                                    )
                                ),

                    exit =
                        fadeOut(spring(stiffness = Spring.StiffnessHigh)) +
                                slideOutVertically(
                                    targetOffsetY = { it / 4 },
                                    animationSpec = spring(stiffness = Spring.StiffnessHigh)
                                ) +
                                scaleOut(
                                    targetScale = 0.98f,
                                    transformOrigin = TransformOrigin(0.5f, 1f),
                                    animationSpec = spring(stiffness = Spring.StiffnessHigh)
                                )
                ) {
                    Text(
                        text = if (quizMode== QuizMode.SYNC)
                            stringResource(R.string.sync_quiz_description) else
                            stringResource(R.string.async_quiz_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .widthIn(max = 200.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickableNoIndication { visibility.targetState = false }
                    )
                }
            }
        }
    }
}