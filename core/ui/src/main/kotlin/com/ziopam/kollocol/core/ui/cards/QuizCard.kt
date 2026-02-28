package com.ziopam.kollocol.core.ui.cards

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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ziopam.kollocol.core.ui.R
import com.ziopam.kollocol.core.ui.clickableNoIndication
import com.ziopam.kollocol.core.ui.preview.quizzesInfoExample
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.domain.model.QuizInfo
import com.ziopam.kollocol.domain.model.QuizMode

private val infoIconSize = 15.dp

@Composable
fun QuizCard(
    quizInfo: QuizInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .clickableNoIndication(onClick)
                .padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = quizInfo.accessCode,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatLine(text = quizInfo.totalQuestions.toString(), icon = ImageVector.vectorResource(R.drawable.question_in_circle_filled))
                    StatLine(text = quizInfo.totalTime, icon = ImageVector.vectorResource(R.drawable.clock_filled))
                    StatLine(text = quizInfo.deadline, icon = ImageVector.vectorResource(R.drawable.calendar))
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (quizInfo.mode == QuizMode.SYNC) stringResource(R.string.sync_quiz) else stringResource(R.string.async_quiz),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )

                InfoIcon(
                    infoText = if (quizInfo.mode == QuizMode.SYNC)
                    stringResource(R.string.sync_quiz_description) else
                    stringResource(R.string.async_quiz_description)
                )
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quizInfo.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 16.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.chevron_right),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun StatLine(
    text: String?,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (text != null) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun InfoIcon(infoText: String) {
    val visibility = remember { MutableTransitionState(false) }

    Box {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.info),
            contentDescription = stringResource(R.string.quiz_mode_info),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            modifier = Modifier
                .size(infoIconSize)
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
                        text = infoText,
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

@Preview
@Composable
private fun QuizCardPreview() {
    AppTheme {
        QuizCard(
            quizInfo = quizzesInfoExample[0],
            onClick = {},
        )
    }
}
