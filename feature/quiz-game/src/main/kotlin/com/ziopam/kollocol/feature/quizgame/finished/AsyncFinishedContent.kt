package com.ziopam.kollocol.feature.quizgame.finished

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.cards.LayoutWithLargeBottomCard
import com.ziopam.kollocol.core.ui.preview.AppPreview
import com.ziopam.kollocol.feature.quizgame.GameHeader
import com.ziopam.kollocol.feature.quizgame.R

@Composable
internal fun AsyncFinishedContent(
    quizName: String,
    onNavigateBack: () -> Unit
) {
    LayoutWithLargeBottomCard(
        scrollable = false,
        contentAbove = { GameHeader(quizName = quizName, onExit = onNavigateBack) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.game_async_completed),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.game_async_await_result),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

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

@PreviewLightDark
@Composable
private fun AsyncFinishedContentPreview() {
    AppPreview {
        AsyncFinishedContent(
            quizName = "Коллоквиум iOS",
            onNavigateBack = {}
        )
    }
}
