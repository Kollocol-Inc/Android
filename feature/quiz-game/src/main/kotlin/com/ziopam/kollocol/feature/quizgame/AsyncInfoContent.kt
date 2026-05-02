package com.ziopam.kollocol.feature.quizgame

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.preview.AppPreview

@Composable
internal fun AsyncInfoContent(
    quizName: String,
    accessCode: String,
    deadline: String?,
    onStart: () -> Unit,
    onNavigateBack: () -> Unit
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
                onExit = onNavigateBack,
                onCopyCode = {
                    clipboardManager.setText(AnnotatedString(accessCode))
                    Toast.makeText(context, codeCopiedText, Toast.LENGTH_SHORT).show()
                }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.game_async_about),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (deadline != null) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.game_async_deadline, deadline),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            DefaultButton(
                text = stringResource(R.string.game_async_start),
                onClick = onStart,
                isButtonEnabled = true,
                isWidthLimited = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AsyncInfoContentPreview() {
    AppPreview {
        AsyncInfoContent(
            quizName = "Коллоквиум iOS",
            accessCode = "127287",
            deadline = "09:41 01.04.2025",
            onStart = {},
            onNavigateBack = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun AsyncInfoContentNoDeadlinePreview() {
    AppPreview {
        AsyncInfoContent(
            quizName = "Коллоквиум iOS",
            accessCode = "127287",
            deadline = null,
            onStart = {},
            onNavigateBack = {}
        )
    }
}
