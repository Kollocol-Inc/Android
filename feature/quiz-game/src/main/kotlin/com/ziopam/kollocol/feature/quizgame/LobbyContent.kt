package com.ziopam.kollocol.feature.quizgame

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.clickableNoIndication
import com.ziopam.kollocol.core.ui.preview.AppPreview
import com.ziopam.kollocol.core.ui.theme.ExtraColors
import com.ziopam.kollocol.domain.model.GameParticipant
import com.ziopam.kollocol.feature.quizgame.preview.GamePreviewData
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun LobbyContent(
    quizName: String,
    accessCode: String,
    lobby: GamePhase.Lobby,
    selfUserId: String = "",
    onStartQuiz: () -> Unit,
    onCancelQuiz: () -> Unit,
    onExitQuiz: () -> Unit = {},
    onKickParticipant: (String) -> Unit = {}
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val codeCopiedText = stringResource(R.string.game_code_copied)

    LayoutWithLargeBottomCard(
        scrollable = false,
        contentAbove = {
            GameHeader(
                quizName = quizName,
                accessCode = accessCode,
                onExit = onExitQuiz,
                onCopyCode = {
                    clipboardManager.setText(AnnotatedString(accessCode))
                    Toast.makeText(context, codeCopiedText, Toast.LENGTH_SHORT).show()
                }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.game_participants),
                    style = MaterialTheme.typography.headlineMedium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${lobby.participantCount}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = ImageVector.vectorResource(id = CoreR.drawable.user_filled),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lobby.participants) { participant ->
                    ParticipantRow(
                        participant = participant,
                        isCreatorView = lobby.isCreator,
                        isSelf = participant.userId == selfUserId,
                        onKick = { onKickParticipant(participant.email) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (lobby.isCreator) {
                val hasEnoughParticipants = lobby.participants.count { !it.isCreator } >= 1
                DefaultButton(
                    text = stringResource(
                        if (hasEnoughParticipants) R.string.game_start_quiz
                        else R.string.game_not_enough_participants
                    ),
                    onClick = onStartQuiz,
                    isButtonEnabled = hasEnoughParticipants,
                    isWidthLimited = false,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                DefaultButton(
                    text = stringResource(R.string.game_cancel_quiz),
                    isButtonEnabled = true,
                    isWidthLimited = false,
                    onClick = onCancelQuiz,
                    buttonColor = Color(0xFFD32F2F),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                DefaultButton(
                    text = stringResource(R.string.game_waiting_for_start),
                    onClick = {},
                    isButtonEnabled = false,
                    isWidthLimited = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ParticipantRow(
    participant: GameParticipant,
    isCreatorView: Boolean,
    isSelf: Boolean,
    onKick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarPicker(
            avatarUrl = participant.avatarUrl,
            onClick = {},
            modifier = Modifier.size(48.dp),
            defaultIconSize = 28.dp
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = participant.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            if (participant.email.isNotBlank()) {
                Text(
                    text = participant.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isSelf -> Text(
                    text = stringResource(R.string.game_you_label),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                isCreatorView && !participant.isCreator -> Icon(
                    imageVector = ImageVector.vectorResource(
                        id = CoreR.drawable.delete
                    ),
                    contentDescription = null,
                    tint = ExtraColors.negative,
                    modifier = Modifier.clickableNoIndication(onClick = onKick)
                )
                participant.isCreator -> Text(
                    text = "\uD83D\uDC51",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LobbyContentPreview(){
    AppPreview {
        LobbyContent(
            "Hello World",
            "123456",
            GamePhase.Lobby(
                participants = GamePreviewData.gameParticipants,
                participantCount = GamePreviewData.gameParticipants.size,
            ),
            GamePreviewData.gameParticipants[1].userId,
            {},
            {},
            {}
        )
    }
}

@PreviewLightDark
@Composable
private fun LobbyContentCreatorPreview(){
    AppPreview {
        LobbyContent(
            "Hello World",
            "123456",
            GamePhase.Lobby(
                participants = GamePreviewData.gameParticipants,
                participantCount = GamePreviewData.gameParticipants.size,
                isCreator = true
            ),
            GamePreviewData.gameParticipants.first().userId,
            {},
            {},
            {}
        )
    }
}
