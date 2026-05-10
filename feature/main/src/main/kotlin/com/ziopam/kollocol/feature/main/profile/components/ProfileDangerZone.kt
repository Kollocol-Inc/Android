package com.ziopam.kollocol.feature.main.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.feature.main.R

@Composable
internal fun ProfileDangerZone(onDeleteAccount: () -> Unit) {
    OutlinedButton(
        onClick = onDeleteAccount,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, Color(0xFFE53935))
    ) {
        Text(
            text = stringResource(R.string.delete_account),
            color = Color(0xFFE53935),
            style = MaterialTheme.typography.labelLarge
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}
