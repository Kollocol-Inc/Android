package com.ziopam.kollocol.feature.main.groups.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ziopam.kollocol.core.ui.buttons.DefaultButton
import com.ziopam.kollocol.feature.main.R
import com.ziopam.kollocol.feature.main.groups.components.EmailsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InviteMembersSheet(
    onDismiss: () -> Unit,
    onInvite: (List<String>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var emails by remember { mutableStateOf(listOf("")) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetGesturesEnabled = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(Modifier.weight(0.5f))
                Text(
                    text = stringResource(R.string.invite_members_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(Modifier.weight(0.5f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            EmailsSection(
                emails = emails,
                onAddEmail = { emails = emails + "" },
                onEmailChange = { index, newVal ->
                    emails = emails.toMutableList().also { it[index] = newVal }
                },
                onRemoveEmail = { index ->
                    emails = emails.toMutableList().also { it.removeAt(index) }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            DefaultButton(
                text = stringResource(R.string.invite_button),
                onClick = { onInvite(emails) },
                isButtonEnabled = emails.any { it.isNotBlank() },
                isWidthLimited = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
