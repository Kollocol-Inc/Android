package com.ziopam.kollocol.feature.main.home

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.avatar.AvatarPicker
import com.ziopam.kollocol.core.ui.buttons.CircleIconButton
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.core.ui.contentPadding
import com.ziopam.kollocol.core.ui.input.OtpCodeInput
import com.ziopam.kollocol.core.ui.theme.AppTheme
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.core.ui.R as CoreR

@Composable
fun HomeAbove(
    personName: String,
    code: String = "",
    onCodeChanged: (String) -> Unit = {},
    avatarUri: Uri? = null,
    onClick: () -> Unit = {},
    onJoinQuiz: () -> Unit = {}
){
    Column(
        modifier = Modifier.padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AvatarPicker(
                avatarUrl = avatarUri?.toString(),
                onClick = onClick,
                modifier = Modifier.size(48.dp),
                defaultIconSize = 24.dp
            )
            Text(
                text = personName,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(Modifier.weight(1f))
            CircleIconButton(
                onClick = {},
                icon = ImageVector.vectorResource(id = CoreR.drawable.bell),
                contentDescription = stringResource(R.string.notification),
            )

        }

        Spacer(Modifier.height(3.dp))

        OtpCodeInput(
            code = code,
            onCodeChange = onCodeChanged,
            onComplete = {},
            cellsAmount = 6,
            shape = RoundedCornerShape(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            borderColor = MaterialTheme.colorScheme.outline,
            cellColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            useActive = false,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        DefaultButton(
            text = stringResource(R.string.start_quiz),
            onClick = onJoinQuiz,
            isButtonEnabled = code.length == 6,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeAbovePreview(){
    AppTheme { HomeAbove("Павел Попов") }
}