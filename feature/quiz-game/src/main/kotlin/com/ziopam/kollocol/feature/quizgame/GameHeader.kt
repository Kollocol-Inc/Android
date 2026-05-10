package com.ziopam.kollocol.feature.quizgame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.core.ui.contentPadding
import com.ziopam.kollocol.core.ui.theme.ExtraColors
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
internal fun GameHeader(
    quizName: String,
    accessCode: String? = null,
    onCopyCode: (() -> Unit)? = null,
    onExit: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding() / 2
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(
            { onExit?.invoke() },
            icon = ImageVector.vectorResource(id = CoreR.drawable.exit),
            contentDescription = stringResource(R.string.game_leave_quiz),
            tint = ExtraColors.negative
        )

        Column(
            modifier = Modifier.padding(horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = quizName,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 2,
                fontWeight = FontWeight.Bold
            )

            if (accessCode != null) {
                Text(
                    text = accessCode,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (onCopyCode != null) {
            CircleIconButton(
                onCopyCode,
                icon = ImageVector.vectorResource(id = R.drawable.copy),
                contentDescription = stringResource(R.string.game_copy_code),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Box(modifier = Modifier.size(48.dp))
        }
    }
}
